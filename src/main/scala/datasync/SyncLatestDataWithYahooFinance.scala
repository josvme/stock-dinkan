package datasync

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import downloader.{DownloaderYahoo, YahooStockConfig}
import migrations.JdbcDatabaseConfig
import models.DayData
import saver.DatabaseReadWritePort
import transformers.JsonToDayData
import cats.implicits._
import fs2.Stream

import java.time.{DayOfWeek, Instant, LocalTime, ZoneId, ZoneOffset}
import scala.concurrent.duration.DurationInt

object SyncLatestDataWithYahooFinance extends App {

  println("Welcome to StockDinkan Yahoo Finance Data Sync")
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val dbPort: IO[DatabaseReadWritePort[IO]] = ixa
    .map(xa => {
      val port = new DatabaseReadWritePort[IO](xa)
      port
    })

  def syncData() = {
    // get all latest data from DB for a stock and then sync it with the latest date
    val currentTime = Instant.now()
    import java.time.LocalTime
    import java.time.LocalDateTime

    val now = LocalDateTime.now() //.`with`(LocalTime.MAX)
    val epoch = now.toEpochSecond(ZoneOffset.UTC)
    val allStocks = dbPort.flatMap(_.getAllStocks)
    val allStocksStream = Stream.eval(allStocks).flatMap(Stream.emits)
    val allStocksPeriods =
      allStocksStream.evalMap(symbol => getDatePeriodsForStock(symbol, epoch))
    // We want to rate limit only stocks that should be downloaded
    val allStocksPeriodStreamPerSecond = allStocksPeriods
      .filter({ case (_, config) => !dataAlreadyDownloaded(config) })
      .metered(1.second)
    val fetchedStocks = allStocksPeriodStreamPerSecond.parEvalMap(1)(period =>
      getAndWriteStockData(period._1, period._2)
    )
    val retryFetchedStocksStream = Stream
      .retry(
        fetchedStocks.compile.toList,
        delay = 1.second, // delay before first retry
        nextDelay = _ * 3, // triples the delay for every retry
        maxAttempts = 4,
        _ => true // retry on any error
      )

    retryFetchedStocksStream
  }

  def getAndWriteStockData(
      symbol: String,
      config: YahooStockConfig
  ): IO[List[Option[Int]]] = {
    val dayDatas = getStockData(symbol, config)
    val writes = for {
      dayData <- dayDatas
      port <- dbPort
      v <- dayData.map(port.writeDayData).sequence
    } yield (v)

    println(symbol)
    writes
  }

  def getDatePeriodsForStock(
      symbol: String,
      endData: Long
  ): IO[(String, YahooStockConfig)] = {
    val currentTime = endData
    val latestTimeInStock = dbPort.flatMap(_.getLatestPointForStock(symbol))

    latestTimeInStock.map(l =>
      (
        symbol,
        YahooStockConfig("1d", l.toString, currentTime.toString)
      )
    )
  }

  def getStartOfDay(s: Long) = {
    val ldt = Instant
      .ofEpochSecond(s)
      .atZone(ZoneId.of("UTC"))
      .toLocalDateTime
      .`with`(LocalTime.MIN)
      .toEpochSecond(ZoneOffset.UTC)
    ldt
  }

  def dataAlreadyDownloaded(config: YahooStockConfig): Boolean = {
    val startDate = Instant
      .ofEpochSecond(config.start.toLong)
      .atZone(ZoneId.of("UTC"))
      .toLocalDateTime
    val endDate = Instant
      .ofEpochSecond(config.end.toLong)
      .atZone(ZoneId.of("UTC"))
      .toLocalDateTime

    // Return true if less than a day old or if data is less than 3 days old and last downloaded day was a friday
    (config.end.toInt - config.start.toInt <= 3600 * 24) || (config.end.toLong - config.start.toLong) < (60 * 60 * 24 * 3) && startDate.getDayOfWeek == DayOfWeek.FRIDAY
  }

  def getStockData(
      symbol: String,
      config: YahooStockConfig
  ): IO[List[DayData]] = {
    if (dataAlreadyDownloaded(config)) {
      IO.pure(List.empty)
    } else {
      val endTime = Instant
        .ofEpochSecond(config.end.toLong)
        .atZone(ZoneId.of("UTC"))
        .toLocalDateTime
        .`with`(LocalTime.MAX)
      val newConfig = YahooStockConfig(
        "1d",
        config.start,
        endTime.toEpochSecond(ZoneOffset.UTC).toString
      )
      val stockData =
        DownloaderYahoo.downloadStockData(symbol, newConfig).map(_.toOption)
      val stockDataList = stockData.map(s =>
        s.map(ss => JsonToDayData.parseJson(ss, symbol))
          .getOrElse(List[DayData]())
      )
      stockDataList
    }
  }

  syncData().compile.drain.unsafeRunSync()
}

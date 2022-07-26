package datasync

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import downloader.{DownloaderYahoo, StockList, YahooStockConfig}
import migrations.JdbcDatabaseConfig
import models.DayData
import saver.DatabaseReadWritePort
import transformers.JsonToDayData
import cats.implicits._
import fs2.Stream

import java.time.{
  DayOfWeek,
  Instant,
  LocalDateTime,
  LocalTime,
  ZoneId,
  ZoneOffset
}
import scala.concurrent.duration.DurationInt

object SyncLatestDataWithYahooFinance extends App {

  println("Welcome to StockDinkan Yahoo Finance Data Sync")
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor[IO](jdbc))

  val dbPort: IO[DatabaseReadWritePort[IO]] = ixa
    .map(xa => {
      val port = new DatabaseReadWritePort[IO](xa)
      port
    })

  def syncData() = {
    // get all latest data from DB for a stock and then sync it with the latest date
    // It is in UTC
    val currentTime = Instant.now()

    val epoch = currentTime.toEpochMilli() / 1000 //.`with`(LocalTime.MAX)
    val allStocks = StockList.getAllStocks()
    val allStocksStream = Stream.eval(allStocks).flatMap(Stream.emits)
    val allStocksPeriods =
      allStocksStream.evalMap(symbol => getDatePeriodsForStock(symbol, epoch))
    // We want to rate limit only stocks that should be downloaded
    val allStocksPeriodStreamPerSecond = allStocksPeriods
      .filter({ case (_, config) => !dataAlreadyDownloaded(config) })
      .metered(1000.millisecond)
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
    val combined = getStockDataDownloadConfig(symbol, config)
    val dayDatas = downloadStockData(combined._1, combined._2)
    val writes = for {
      dayData <- dayDatas
      port <- dbPort
      v <- (port.writeDayDataList(dayData))
    } yield (v)

    println(symbol)
    writes
  }

  def getDatePeriodsForStock(
      symbol: String,
      endData: Long
  ): IO[(String, YahooStockConfig)] = {
    val startDate = Instant
      .ofEpochSecond(endData)
      .atZone(ZoneId.of("UTC"))
      .toLocalDateTime

    val currentHour = startDate.getHour
    val currentDay = startDate.getDayOfWeek
    var currentTime = endData
    val latestTimeInStock: IO[Long] =
      dbPort.flatMap(_.getLatestPointForStock(symbol))
    val notInTradingTime = (currentHour < 13 || currentHour > 21)
    val notInTradingDays =
      currentDay == DayOfWeek.SUNDAY || currentDay == DayOfWeek.SATURDAY

    if (notInTradingDays || notInTradingTime) {
      currentTime = endData
    } else {
      currentTime = startDate
        .minusDays(1)
        .`with`(LocalTime.MAX)
        .toInstant(ZoneOffset.UTC)
        .getEpochSecond
    }
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
    (config.end.toInt - config.start.toInt <= 3600 * 24) || ((config.end.toLong - config.start.toLong) < (60 * 60 * 24 * 3) && startDate.getDayOfWeek == DayOfWeek.FRIDAY)
  }

  def isTradingHappeningNow(time: LocalDateTime): Boolean = {
    val isTradingDay =
      time.getDayOfWeek != DayOfWeek.SATURDAY && time.getDayOfWeek != DayOfWeek.SUNDAY
    val insideTradingTime =
      ((time.getHour == 14 && time.getMinute >= 30) || (time.getHour > 14)) && (time.getHour <= 20)

    isTradingDay && insideTradingTime
  }

  def getStockDataDownloadConfig(
      symbol: String,
      config: YahooStockConfig
  ): (String, YahooStockConfig) = {
    var currentTime = Instant
      .ofEpochSecond(config.end.toLong)
      .atZone(ZoneId.of("UTC"))
      .toLocalDateTime

    if (isTradingHappeningNow(currentTime)) {
      currentTime = currentTime.`with`(LocalTime.MIN)
    }

    val newConfig = YahooStockConfig(
      "1d",
      config.start,
      currentTime.toEpochSecond(ZoneOffset.UTC).toString
    )
    (symbol, newConfig)
  }

  private def downloadStockData(symbol: String, newConfig: YahooStockConfig) = {
    val stockData =
      DownloaderYahoo.downloadStockData(symbol, newConfig).map(_.toOption)
    val stockDataList = stockData.map(s =>
      s.map(ss => JsonToDayData.parseJson(ss, symbol))
        .getOrElse(List[DayData]())
    )
    stockDataList
  }

  syncData().compile.drain.unsafeRunSync()
}

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

import java.time.Instant

object SyncLatestDataWithYahooFinance extends App {

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
    val allStocks = dbPort.flatMap(_.getAllStocks)
    val allStocksPeriods = allStocks.flatMap(stocks =>
      stocks.map(symbol => getDatePeriodsForStock(symbol, currentTime)).sequence
    )
    val writtenStocks = allStocksPeriods.flatMap(periods =>
      periods.map(period => getAndWriteStockData(period._1, period._2)).sequence
    )
    writtenStocks
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
      endData: Instant
  ): IO[(String, YahooStockConfig)] = {
    val currentTime = endData.getEpochSecond
    val latestTimeInStock = dbPort.flatMap(_.getLatestPointForStock(symbol))

    latestTimeInStock.map(l =>
      (symbol, YahooStockConfig("1d", currentTime.toString, l.toString))
    )
  }

  def getStockData(
      symbol: String,
      config: YahooStockConfig
  ): IO[List[DayData]] = {
    val stockData =
      DownloaderYahoo.downloadStockData(symbol, config).map(_.toOption)
    val stockDataList = stockData.map(s =>
      s.map(ss => JsonToDayData.parseJson(ss, symbol))
        .getOrElse(List[DayData]())
    )
    stockDataList
  }

  syncData().unsafeRunSync()
}

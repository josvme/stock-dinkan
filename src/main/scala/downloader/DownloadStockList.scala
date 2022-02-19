package downloader

import cats.effect.IO
import cats.implicits._
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData
import fs2.Stream

import scala.io.Source
import io.circe._
import io.circe.parser.{parse, _}
import io.circe.optics.JsonPath._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Using}


object DownloadStockList extends App {

  val stockListFile = s"./stock-files/stocks.json"
  val config = StockConfig.getConfig
  val stocks = IO.delay({
    val stockFile = Using(Source.fromFile(stockListFile)) {s => s.getLines().mkString}

    val stockListJson = stockFile match {
      case Failure(exception) => None
      case Success(value) => parse(value).toOption
    }

    val listSelector = root.arr
    val k = for {
      s <- stockListJson
      ss <- listSelector.getOption(s)
    } yield ss

    k.getOrElse(Vector[Json]()).map(x => x.asString.get)
  })

  // Sent only 1 entry per second
  val stocksStream = Stream.eval(stocks).flatMap(Stream.emits).metered(1.second)
  val downloadStocksStream = stocksStream.parEvalMap(2)(t => Downloader.downloadFile(t, config).map(xx => {
    println(t); (xx, t)
  }))

  val downloadStocksList = downloadStocksStream.compile.toList

  val jdbcConfig: IO[JdbcDatabaseConfig] = JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] = jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)

    val readStocks = downloadStocksList.map((stocks => stocks.map(f => (f._1.map(Source.fromFile(_).mkString), f._2))))
    val parsedStocks = readStocks.map(f => f.map(x => x._1.map(xx => JsonToDayData.parseJson(xx, x._2))))

    val xxx = parsedStocks.map(t => t.map(tt => tt.map(ttt => ttt.map(tttt => db.writeDayData(tttt).unsafeRunSync()))))
    xxx
  })

  xxxx.unsafeRunSync()
}

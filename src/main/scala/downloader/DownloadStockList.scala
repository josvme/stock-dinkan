package downloader

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import cats.effect.unsafe.implicits.global
import doobie.hi.resultset.getOption
import doobie.util.transactor.Transactor.Aux
import downloader.Main.{download, symbol}
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData

import java.io.File
import scala.io.Source
import io.circe._
import io.circe.parser._
import models.DayData
import io.circe.optics.JsonPath._


object DownloadStockList extends App {

  val stockListFile = s"./stock-files/stocks.json"
  val stocks = IO.delay({
    val stockListJson = parse(Source.fromFile(stockListFile).getLines().mkString).toOption
    val listSelector = root.arr
    val k = for {
      s <- stockListJson
      ss <- listSelector.getOption(s)
    } yield ss

    k.getOrElse(Vector[Json]()).map(x => x.asString.get)
  })

  val downloadedStocks = stocks.flatMap(x => x.map(t => Downloader.downloadFile(t).map(xx => (xx, t))).sequence)

  val jdbcConfig: IO[JdbcDatabaseConfig] = JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] = jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)

    val readStocks = downloadedStocks.map((stocks => stocks.map(f => (f._1.map(Source.fromFile(_).mkString), f._2))))
    val parsedStocks = readStocks.map(f => f.map(x => x._1.map(xx => JsonToDayData.parseJson(xx, x._2))))

    val xxx = parsedStocks.map(t => t.map(tt => tt.map(ttt => ttt.map(tttt => db.writeDayData(tttt).unsafeRunSync()))))
    xxx
  })

  xxxx.unsafeRunSync()
}

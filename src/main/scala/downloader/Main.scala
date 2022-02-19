package downloader

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData

import java.io.File
import scala.io.Source

object Main extends App {

  val fileLocation = s"./stock-files/"
  val symbol = "AAPL"
  val config = StockConfig.getConfig
  val download: IO[Either[String, File]] =
    Downloader.downloadFile(symbol, config)

  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)

    val xx = for {
      f <- EitherT(download)
      x <- EitherT(
        IO.delay(Source.fromFile(f).getLines().mkString.asRight[String])
      )
      yy <- EitherT(
        IO.delay(JsonToDayData.parseJson(x, symbol).asRight[String])
      )
    } yield yy

    val tt = xx.value
    val xxx = tt.map(t =>
      t.map(tt => tt.map(ttt => db.writeDayData(ttt).unsafeRunSync()))
    )
    xxx
  })

  xxxx.unsafeRunSync()
}

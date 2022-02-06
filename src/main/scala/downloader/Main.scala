package downloader

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import cats.syntax._
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import downloader.Main.xxxx
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData

import java.io.File
import scala.io.Source

object Main extends App {

  val fileLocation = s"./stock-files/"
  val symbol = "AAPL"
  val download: IO[Either[String, File]] = Downloader.downloadFile(symbol)

  val jdbcConfig: IO[JdbcDatabaseConfig] = JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] = jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)

    val xx = for {
      f <- EitherT(download)
      x <- EitherT(IO.delay(Source.fromFile(f).getLines().mkString.asRight[String]))
      yy <- EitherT(IO.delay(JsonToDayData.parseJson(x, symbol).asRight[String]))
    } yield yy

    val xxx = xx.map(t => t.map(tt => db.writeDayData(tt).unsafeRunSync())).value
    xxx
  })

  xxxx.unsafeRunSync()

}

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.util.transactor.Transactor.Aux
import filesources.DataSource
import fs2.Stream
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData

import scala.io.Source

object ReadAndWriteStocksToDB extends App {
  println("Welcome to Stock Dinkan")

  val allFiles = DataSource.getAllStockFileNames[IO]
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  def getStockName(getName: String) = {
    getName.takeWhile(x => x != '.')
  }

  ixa
    .flatMap(xa => {
      val port = new DatabaseReadWritePort[IO](xa)
      val contents = {
        allFiles.flatMap(files =>
          Stream(files: _*)
            .evalMap(file => {
              val contents = Source.fromFile(file).mkString
              val symbol = file.getName.stripSuffix(".json")
              val json = JsonToDayData
                .parseJson(contents, symbol)
              val writeJson = json.map(port.writeDayData(_)).sequence
              writeJson
            })
            .compile
            .toList
        )
      }
      contents
    })
    .map(println)
    .unsafeRunSync()
}

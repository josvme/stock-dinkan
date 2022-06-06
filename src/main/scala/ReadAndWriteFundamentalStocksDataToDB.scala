import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.util.transactor.Transactor.Aux
import filesources.DataSource
import fs2.Stream
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import transformers.JsonToDayData

import io.circe._
import io.circe.parser._

import scala.io.Source

object ReadAndWriteFundamentalStocksDataToDB extends App {
  println("Welcome to Stock Dinkan Fundamentals DB Writer")

  val allFiles = DataSource.getAllStockFundamentalFileNames[IO]
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
              val json = parse(contents).getOrElse(Json.Null)
              val symbol = file.getName.stripSuffix(".json")
              val writeJson = port.writeFundamentals(symbol, json)
              println(symbol)
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

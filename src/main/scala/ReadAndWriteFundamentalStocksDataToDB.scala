import cats.effect.IO
import filesources.DataSource
import fs2.Stream
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import cats.effect.unsafe.implicits.global
import io.circe._
import io.circe.parser._

import java.time.Instant
import scala.io.Source

@main def main() = {
  println("Welcome to Stock Dinkan Fundamentals DB Writer")
  val startTime = Instant.now.getEpochSecond
  val allFiles = DataSource.getAllStockFundamentalFileNames[IO]
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor[IO](jdbc))

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
              val writeJson = port.writeFundamentals(symbol, json, startTime)
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

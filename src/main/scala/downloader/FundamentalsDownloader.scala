package downloader

import cats.effect.IO
import cats.implicits._
import me.shadaj.scalapy.py

import java.io.File
import java.nio.file.{Files, Paths}
import scala.util.Try
import io.circe._, io.circe.parser._

object FundamentalsDownloader {

  def downloadFile(
      ticker: String
  ): IO[Either[String, File]] = {
    val fileLocation = s"./stock-fundamental-files/${ticker}.json"
    IO.delay({
      Try {
        val yfinance = py.module("yfinance")
        val json = py.module("json")
        val data = yfinance.Ticker(ticker)
        val earnings = data.earnings_history.to_json().as[String]
        val info = json.dumps(data.info).as[String]

        val earningsJson = parse(earnings)
        val infoJson = parse(info)

        val combinedJson = for {
          e <- earningsJson
          i <- infoJson
        } yield e.deepMerge(i)

        val combined =
          combinedJson.getOrElse(Json.Null).toString
        val path = Paths.get(fileLocation)
        Try {
          Files.delete(path)
        }.toOption
        Files.write(
          path,
          combined.getBytes()
        )
        path.toFile
      }.toEither.leftMap(_.getMessage)
    })
  }
}

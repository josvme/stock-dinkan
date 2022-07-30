package downloader

import cats.effect.IO
import cats.implicits._
import me.shadaj.scalapy.py

import java.io.File
import java.nio.file.{Files, Paths}
import scala.util.Try

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
        val info = json.dumps(data.info).as[String]
        val path = Paths.get(fileLocation)
        Try {
          Files.delete(path)
        }.toOption
        Files.write(path, info.getBytes)
        path.toFile
      }.toEither.leftMap(_.getMessage)
    })
  }
}

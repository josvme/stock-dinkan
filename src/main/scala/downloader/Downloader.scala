package downloader

import cats.effect.IO
import sttp.client3.{Response, UriContext, asFile, emptyRequest}
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend

import java.io.File


// https://data.alpaca.markets/v2/stocks/ZIM/bars?timeframe=1Day&start=2020-04-01&end=2021-08-26

object Downloader {

  def downloadFile(ticker: String): IO[Either[String, File]] = {
    val fileLocation = s"./stock-files/${ticker}.json"
    AsyncHttpClientFs2Backend.resource[IO]().use { backend =>
     emptyRequest
        .header("APCA-API-KEY-ID", RequestConfig.APCA_API_KEY_ID)
        .header("APCA-API-SECRET-KEY", RequestConfig.APCA_API_SECRET_KEY)
        .contentType("application/json")
        .get(uri"${RequestConfig.APCA_API_BASE_URL}/$ticker/bars?timeframe=${StockConfig.timeFrame}&start=${StockConfig.start}&end=${StockConfig.end}")
        .response(asFile(new File(fileLocation)))
        .send(backend)
       .map(_.body)
    }
  }
}
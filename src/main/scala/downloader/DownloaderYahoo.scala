package downloader

import cats.effect.IO
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client3.{UriContext, asFile, emptyRequest}

object DownloaderYahoo {

  def downloadStockData(
      ticker: String,
      config: YahooStockConfig
  ): IO[Either[String, String]] = {
    AsyncHttpClientFs2Backend.resource[IO]().use { backend =>
      val request = emptyRequest
        .contentType("application/json")
        .get(
          uri"${RequestConfigYahoo.YAHOO_BASE_URL}/$ticker?interval=${config.timeFrame}&period1=${config.start}&period2=${config.end}&includePrePost=False&events=div%2Csplits"
        )

      val response = request.send(backend)
      response.map(_.body)
    }
  }
}

package downloader

import cats.effect.IO
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client3.{UriContext, asFile, emptyRequest}

import java.io.File

// https://data.alpaca.markets/v2/stocks/ZIM/bars?timeframe=1Day&start=2020-04-01&end=2021-08-26

object DownloaderYahoo {

  def downloadFile(
      ticker: String,
      config: YahooStockConfig
  ): IO[Either[String, File]] = {
    val fileLocation = s"./stock-files/${ticker}.json"
    AsyncHttpClientFs2Backend.resource[IO]().use { backend =>
      val request = emptyRequest
        .contentType("application/json")
        .get(
          uri"${RequestConfigYahoo.YAHOO_BASE_URL}/$ticker?interval=${config.timeFrame}&period1=${config.start}&period2=${config.end}&includePrePost=False&events=div%2Csplits"
          //https://query1.finance.yahoo.com/v8/finance/chart/LTHM?period1=1434837600&period2=1649432581&useYfid=true&interval=1d&includePrePost=true&events=div|split|earn&lang=en-US&region=US&crumb=3ZAhcXx5tlP&corsDomain=finance.yahoo.com"
        )
        .response({
          val f = new File(fileLocation)
          f.delete()
          asFile(f)
        })

      val response = request.send(backend)
      response.map(_.body)
    }
  }

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

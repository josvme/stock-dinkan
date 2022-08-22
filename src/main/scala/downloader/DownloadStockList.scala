package downloader

import cats.effect.IO
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client3.{UriContext, basicRequest, emptyRequest}
import cats.effect.unsafe.implicits.global
import cats.implicits.toTraverseOps
import io.circe.optics.JsonPath.root
import io.circe.{Json, parser}

object StockList {
  def downloadStockList() = {
    AsyncHttpClientFs2Backend.resource[IO]().use { backend =>
      val request = basicRequest
        .header("Accept", "text/html")
        .header("User-Agent", "Mozilla/5.0")
        .header("Connection", "keep-alive")
        .header("Host", "api.nasdaq.com")
        .followRedirects(true)
        .get(
          uri"https://api.nasdaq.com/api/screener/stocks?tableonly=true&limit=10000&offset=0"
          // https://api.nasdaq.com/api/screener/stocks?tableonly=true&limit=25&offset=0&download=true
        )
      val response = request.send(backend)
      response.map(_.body)
    }
  }

  def getAllStocks(): IO[Vector[String]] = {
    val stocks: IO[Option[Json]] =
      StockList
        .downloadStockList()
        .map(_.toOption.flatMap(x => parser.parse(x).toOption))
    val listSelector = root.data.table.rows.arr
    val stocksList = stocks.map {
      case Some(s) => listSelector.getOption(s)
      case None    => None
    }
    val symbols = stocksList
      .map(x =>
        x.flatMap(v => v.map(vv => root.symbol.string.getOption(vv)).sequence)
      )
      .map(x => x.getOrElse(Vector()))
      .map(list => list.sorted)
      .map(l => l.filter((x) => !x.contains("^")))
    symbols.map(x => "QQQ" +: x)
  }
}

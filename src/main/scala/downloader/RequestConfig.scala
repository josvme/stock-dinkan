package downloader

object RequestConfig {
  val APCA_API_KEY_ID = "PK13F9SJR46LIVGS731N"
  val APCA_API_SECRET_KEY = "1LQH948CtZe0LET7BTi7rxHxHo31ondsQdndhw3q"
  val APCA_API_BASE_URL = "https://data.alpaca.markets/v2/stocks"
}

case class StockConfig(timeFrame: String, start: String, end: String)
object StockConfig {
  def getConfig = {
    val timeFrame = "1Day"
    val start = "2020-01-01"
    val end = "2022-02-18"

    StockConfig(timeFrame, start, end)
  }
}
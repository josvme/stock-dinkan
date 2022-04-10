package downloader

import java.time.{LocalDate, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object RequestConfigYahoo {
  val YAHOO_BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart"
}

case class YahooStockConfig(timeFrame: String, start: String, end: String)
object YahooStockConfig {
  def getConfig = {
    val timeFrame = "1d"
    val start = dateTimeStringToEpoch("2020/03/18", "yyyy/MM/dd")
    val end = dateTimeStringToEpoch("2022/04/09", "yyyy/MM/dd")

    YahooStockConfig(timeFrame, start.toString, end.toString)
  }

  def dateTimeStringToEpoch(s: String, pattern: String): Long = {
    LocalDate
      .parse(s, DateTimeFormatter.ofPattern(pattern))
      .atStartOfDay()
      .toEpochSecond(ZoneOffset.UTC)
  }
}

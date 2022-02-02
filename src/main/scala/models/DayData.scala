package models

case class DayData(time: Long, open: Float, close: Float, low: Float, high: Float, volume: Int, trade_count: Int, vwap: Float, symbol: String)

case class DayDataExtras(time: Long, symbol: String)

package models

case class DayData(id: Int, symbol: String, stime: Long, sopen: Float, sclose: Float, low: Float, high: Float, volume: Int, trade_count: Int, vwap: Float)

case class DayDataExtras(time: Long, symbol: String)

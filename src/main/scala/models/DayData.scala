package models

case class DayData(
    id: Int,
    symbol: String,
    stime: Long,
    sopen: Double,
    sclose: Double,
    low: Double,
    high: Double,
    volume: Long,
    trade_count: Int,
    vwap: Double
)

case class DayDataExtras(time: Long, symbol: String)

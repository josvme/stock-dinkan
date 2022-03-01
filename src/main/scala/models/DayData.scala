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

case class WeekData(
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

case class DayDataExtras(
    d: DayData,
    drs: Double,
    sma10: Double,
    sma50: Double,
    sma200: Double
)

case class WeekDataExtras(
    d: WeekData,
    wrs: Double,
    wsma10: Double,
    wsma50: Double
)

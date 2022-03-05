package models

case class TimeBasedStockCompareModel(
    stime: Long,
    index: DayData,
    stocks: DayData
)

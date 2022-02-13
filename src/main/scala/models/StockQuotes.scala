package models

case class StockQuotes(symbol: String, json: Vector[DayData])

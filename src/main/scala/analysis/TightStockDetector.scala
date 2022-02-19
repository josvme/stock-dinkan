package analysis
import models.{DayData, StockQuotes}

object TightStockDetector extends AnalysisTrait {
  // stock open-close shouldn't oscillate more than 4% from centre.

  override def passAnalysis(stocks: Vector[DayData]): Boolean = {
    val last10Days = stocks.reverse.take(10)
    val lowestLowPoint = last10Days.reduce((x, y) => if (x.low < y.low) x else y)
    val highestHighPoint = last10Days.reduce((x, y) => if (x.high > y.high) x else y)
    val middlePriceForLast10Days= last10Days.foldRight(0.0)((x,e) => x.low + x.high + e) / 2 / 10

    val diffHighLow = highestHighPoint.high - lowestLowPoint.low
    // Approximately 3*2 => 6%
    diffHighLow < middlePriceForLast10Days * .03
  }
}

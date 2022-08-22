package analysis

import models.DayData

object TightStockDetector extends AnalysisTrait {
  // stock open-close shouldn't oscillate more than 5% from centre.

  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    val blankListCompanies = List()
    val isABlankCheckCompany =
      stocks.headOption.forall(d => blankListCompanies.contains(d.symbol))

    if (isABlankCheckCompany) {
      return false;
    }

    // Tight action for 5 days
    val last5Days = stocks.reverse.take(5)
    val lowestLowPoint =
      last5Days.reduce((x, y) => if (x.low < y.low) x else y)
    val highestHighPoint =
      last5Days.reduce((x, y) => if (x.high > y.high) x else y)
    val middlePriceForLast10Days =
      last5Days.foldRight(0.0)((x, e) => x.low + x.high + e) / 2 / 5

    val diffHighLow = highestHighPoint.high - lowestLowPoint.low
    // Approximately 3*2 => 10%
    diffHighLow < middlePriceForLast10Days * .05
  }

  override def name(): String = "Tight Stock Analyzer"
}

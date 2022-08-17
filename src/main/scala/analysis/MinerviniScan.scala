package analysis

import models.DayData
import utilies.Indicators

class MinerviniScan(stocksAboveSeventy: List[String]) extends AnalysisTrait {
  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {

    if (stocks.length < 1) {
      return false
    }
    val weeklyDailyRS = Indicators.computeWeeklyRS(index, stocks)

    val weeklyPrices = Indicators.computeWeeklyFromDailyData(stocks)
    val currentPrice = stocks.last.sclose
    val last230Days = stocks.reverse.take(230).map(_.sclose)
    val last200Days = stocks.reverse.take(200).map(_.sclose)
    val last150Days = stocks.reverse.take(150).map(_.sclose)
    val last50Days = stocks.reverse.take(50).map(_.sclose)
    val last52Weeks: Vector[Double] =
      weeklyPrices.reverse.take(52).map(_.sclose)

    val last200DaysAvg = last200Days.sum / 200
    val last150DaysAvg = last150Days.sum / 150
    val last50DaysAvg = last50Days.sum / 50
    val last30Days200DaysAvg =
      last230Days.sliding(200).map(_.sum / 200).toVector

    val condition1 =
      currentPrice > last150DaysAvg && currentPrice > last200DaysAvg

    val condition2 = last150DaysAvg > last200DaysAvg

    // 200 day trending up for atleast 1 month
    val condition3 = true || isTrendingUp(last30Days200DaysAvg)

    val condition4 =
      last50DaysAvg > last150DaysAvg && last50DaysAvg > last200DaysAvg

    val condition5 = currentPrice > last50DaysAvg

    val condition6 =
      (currentPrice * 1.3) >= last52Weeks.reduce[Double](Math.min)

    val condition7 =
      currentPrice >= (last52Weeks.reduce[Double](Math.max) * .75)

    // relative strength > 70, develop RS to be between 0 and 100
    val condition8 = stocksAboveSeventy.contains(stocks.last.symbol)

    condition1 && condition2 && condition3 && condition4 && condition5 && condition6 && condition7 && condition8
  }

  private def isTrendingUp(d: Vector[Double]): Boolean = {
    val data = d.reverse.take(30)
    val startPoint = data.head
    val lastPoint = data.last
    var highestPoint = startPoint
    val maxDownMoveAllowed = (lastPoint - startPoint) / 10

    if (lastPoint < startPoint) {
      return false
    }

    for (x <- data) {
      if (x > highestPoint) {
        highestPoint = x
      }

      if ((highestPoint - x) > maxDownMoveAllowed) {
        return false
      }
    }

    true
  }

  override def name(): String = "Minervini Scan"
}

package analysis
import models.DayData
import utilies.Indicators

object IncreasingWeeklyDailyRS extends AnalysisTrait {

  override def name(): String = "Increasing RS weekly"

  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    val weeklyDailyRS = Indicators.computeWeeklyRS(index, stocks)
    val last10Days = weeklyDailyRS.reverse.take(10)
    val leading = last10Days.filter(s => s > 1)
    // Atleast leading index for 8 days out of 10 days and more advance than index
    leading.length >= 8 && last10Days.sum > 10
  }
}

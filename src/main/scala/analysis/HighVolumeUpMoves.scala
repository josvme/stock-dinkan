package analysis

import models.DayData

object HighVolumeUpMoves extends AnalysisTrait {
  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    if (stocks.length < 21) {
      false
    } else {
      val last21Days = stocks.takeRight(21)
      val twentyDaysVolume = stocks.takeRight(20).map(x => x.volume)
      val avg20DaysVolume = twentyDaysVolume.sum / twentyDaysVolume.length

      val last20DaysDifferenceClosingDifference =
        last21Days
          .zip(last21Days.takeRight(20))
          .map(x => (x._2.sclose - x._1.sclose, x._2.volume))

      val upDays = last20DaysDifferenceClosingDifference
        .filter(x => x._1 > 0)

      val downDays = last20DaysDifferenceClosingDifference
        .filter(x => x._1 <= 0)

      if (upDays.isEmpty) {
        false
      } else if (downDays.isEmpty) {
        true
      } else {
        val upVolume = upDays
          .map(x => x._2)
          .sum / upDays.length

        val downVolume = downDays
          .map(x => x._2)
          .sum / downDays.length

        if (avg20DaysVolume * 2 < stocks.last.volume) {
          true
        } else {
          // adding a 1 shouldn't change anything
          upVolume / (downVolume + 1) >= 1.5
        }
      }
    }
  }

  override def name(): String = "High Volume Up Moves"
}

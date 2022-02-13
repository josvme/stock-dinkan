package saver

import models.DayData

object DummyDayDataProvider {

  def generateDummyData(n: Int): List[DayData] = {
    (0 to n).map(time => DayData(time, "AAPL", time, time, time, time, time, time, time, time)).toList
  }
}

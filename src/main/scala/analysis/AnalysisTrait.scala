package analysis

import models.{DayData}

trait AnalysisTrait {

  def passAnalysis(stocks: Vector[DayData]): Boolean
}

package analysis

import models.DayData

trait AnalysisTrait {

  def passAnalysis(index: Vector[DayData], stocks: Vector[DayData]): Boolean
  def name(): String
}

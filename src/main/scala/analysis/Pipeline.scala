package analysis
import models.DayData

class Pipeline(analysis: Map[String, AnalysisTrait]) extends AnalysisTrait {
  def addAnalysis(n: AnalysisTrait): Pipeline = {
    new Pipeline(analysis + (n.name() -> n))
  }
  def passAnalysis(index: Vector[DayData], stocks: Vector[DayData]): Boolean = {
    analysis.foldLeft(true) { case (existing, (_, an)) =>
      existing && an.passAnalysis(index, stocks)
    }
  }

  override def name(): String = {
    analysis.foldLeft("") { case (existing, (name, _)) => existing + name }
  }
}

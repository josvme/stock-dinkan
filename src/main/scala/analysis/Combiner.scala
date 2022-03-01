package analysis
import models.DayData

object Combiner {
  def and(a: AnalysisTrait, b: AnalysisTrait) = And(a, b)
  def or(a: AnalysisTrait, b: AnalysisTrait) = Or(a, b)
}

case class And(a: AnalysisTrait, b: AnalysisTrait) extends AnalysisTrait {
  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    a.passAnalysis(index, stocks) && b.passAnalysis(index, stocks)
  }

  override def name(): String = s"And(${a.name()}, ${b.name()})"
}

case class Or(a: AnalysisTrait, b: AnalysisTrait) extends AnalysisTrait {
  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    a.passAnalysis(index, stocks) || b.passAnalysis(index, stocks)
  }

  override def name(): String = s"Or(${a.name()}, ${b.name()})"
}

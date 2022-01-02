package analysis

import models.StockQuotes

trait AnalysisTrait {

  def passAnalysis(stocks: StockQuotes): Boolean
}

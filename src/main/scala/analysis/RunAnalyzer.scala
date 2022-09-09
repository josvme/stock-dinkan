package analysis

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import utilies.{Fundamentals, Indicators}

import java.time.LocalDateTime

object RunAnalyzer {

  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor[IO](jdbc))

  // We do all of these outside as we dont know how to do memoization. Since IO is lazy it executes queries many times
  val allowedStocks = ixa
    .flatMap(xa => {
      val db = new DatabaseReadWritePort(xa)
      db.getAllStocksWithRsRatingAbove(70)
    })
    .unsafeRunSync()

  val nasdaq = ixa
    .flatMap(xa => {
      val db = new DatabaseReadWritePort(xa)
      db.find("QQQ")
    })
    .unsafeRunSync()

  val blankCheckCompanies =
    new Fundamentals[IO](ixa).getBlankCheckCompanies.unsafeRunSync()

  def runAndGetAnalysisResults(p: AnalysisTrait) = {
    val ixaa = fs2.Stream.eval(ixa)
    val indexWithAllStocks = ixaa
      .flatMap(xa => {
        val db = new DatabaseReadWritePort(xa)
        val allStocks =
          fs2.Stream
            .eval(db.getAllStocksWithPriceMoreThanTen)
            .flatMap(fs2.Stream.emits(_))

        allStocks
      })

    val filteredStocks =
      indexWithAllStocks.filter(s => blankCheckCompanies.contains(s))

    val parallelStocks = filteredStocks
      .parEvalMap(8) { stock =>
        ixa.flatMap(xa => {
          val db = new DatabaseReadWritePort(xa)
          val allStocksData = db.find(stock)

          val tuples = for {
            all <- allStocksData
          } yield (nasdaq, all)

          allStocksData.map(s => (nasdaq, s))
        })
      }

    val result = parallelStocks
      .map(IndexStockPair => {
        // val p = IncreasingWeeklyDailyRS
        val t = Indicators
          .combineIndexAndStockData(
            IndexStockPair._1.toVector,
            IndexStockPair._2.toVector
          )
          .map(x => (x.index, x.stocks))

        (
          p.passAnalysis(t.map(_._1), t.map(_._2)),
          t.headOption.map(_._2.symbol)
        )
      })

    val filteredResult =
      result
        .filter(stock => stock._1)
        .map(t => {
          println(t._2);
          t._2
        })

    filteredResult
  }
}

@main def main() = {
  val startTime = LocalDateTime.now()
  println(startTime)
  println("Welcome to StockDinkan Analyzer")
  val filteredResults = {
    // val minerviniScan = new MinerviniScan(s)
    // val p = minerviniScan
    val p = HighVolumeUpMoves
    val filteredResult = RunAnalyzer.runAndGetAnalysisResults(p)
    filteredResult.compile.toList
  }
  // val p = Combiner.and(TightStockDetector, minerviniScan)

  val output = filteredResults.unsafeRunSync()
  println(output)
  val endTime = LocalDateTime.now()
  println(endTime)
}

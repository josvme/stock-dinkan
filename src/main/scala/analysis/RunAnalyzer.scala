package analysis

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import utilies.Indicators

import java.time.LocalDateTime

object RunAnalyzer extends App {

  val startTime = LocalDateTime.now()
  println(startTime)

  def runAndGetAnalysisResults(p: AnalysisTrait) = {
    val jdbcConfig: IO[JdbcDatabaseConfig] =
      JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
    val ixa =
      jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor[IO](jdbc))

    val ixaa = fs2.Stream.eval(ixa)
    val IndexWithAllStocks = ixaa
      .flatMap(xa => {
        val db = new DatabaseReadWritePort(xa)
        val allStocks =
          fs2.Stream
            .eval(db.getAllStocksWithPriceMoreThanTen)
            .flatMap(fs2.Stream.emits(_))

        allStocks
      })

    val parallelStocks = IndexWithAllStocks
      .parEvalMap(16) { stock =>
        ixa.flatMap(xa => {
          val db = new DatabaseReadWritePort(xa)
          val nasdaq = db.find("QQQ")
          val allStocksData = db.find(stock)

          val tuples = for {
            nas <- nasdaq
            all <- allStocksData
          } yield (nas, all)

          tuples
        })
      }

    val result = parallelStocks
      .map(IndexStockPair => {
        //val p = IncreasingWeeklyDailyRS
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

  println("Welcome to StockDinkan Analyzer")
  val p = Combiner.and(TightStockDetector, MinerviniScan)
  val filteredResult = runAndGetAnalysisResults(p)

  filteredResult.compile.toList.unsafeRunSync
  val endTime = LocalDateTime.now()
  println(endTime)
}

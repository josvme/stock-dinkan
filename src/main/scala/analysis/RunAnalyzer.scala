package analysis

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort
import utilies.Indicators

object RunAnalyzer extends App {

  println("Welcome to StockDinkan Analyzer")
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor[IO](jdbc))

  val ixaa = fs2.Stream.eval(ixa)
  val IndexWithAllStocks = ixaa.flatMap(xa => {
    val db = new DatabaseReadWritePort(xa)
    val allStocks =
      fs2.Stream
        .eval(db.getAllStocksWithPriceMoreThanTen)
        .flatMap(fs2.Stream.emits(_))
    val nasdaq = fs2.Stream.eval(db.find("QQQ"))
    val allStocksData =
      allStocks.evalMap(stock => db.find(stock))

    val tuples = for {
      nas <- nasdaq
      all <- allStocksData
    } yield (nas, all)

    tuples
  })

  val result = IndexWithAllStocks.map(IndexStockPair => {
    val p = Combiner.and(TightStockDetector, MinerviniScan)
    //val p = IncreasingWeeklyDailyRS
    val t = Indicators
      .combineIndexAndStockData(
        IndexStockPair._1.toVector,
        IndexStockPair._2.toVector
      )
      .map(x => (x.index, x.stocks))

    (p.passAnalysis(t.map(_._1), t.map(_._2)), t.headOption.map(_._2.symbol))
  })

  val filteredResult =
    result.filter(stock => stock._1).map(t => { println(t._2); t._2 })

  filteredResult.compile.toList.unsafeRunSync
}

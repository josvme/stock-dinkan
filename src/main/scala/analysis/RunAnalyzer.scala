package analysis

import cats.effect.IO
import cats.implicits._
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
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val IndexWithAllStocks = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)
    val allStocks = db.getAllStocks
    val nasdaq = db.find("QQQ")
    val allStocksData =
      allStocks.flatMap(stocks => stocks.map(db.find(_)).sequence)

    for {
      nas <- nasdaq
      all <- allStocksData
    } yield (nas, all)
  })

  val result = IndexWithAllStocks.map(IndexStockPair =>
    IndexStockPair._2.map(stock => {
      val p = Combiner.and(IncreasingWeeklyDailyRS, MinerviniScan)
      //val p = IncreasingWeeklyDailyRS
      val t = Indicators
        .combineIndexAndStockData(
          IndexStockPair._1.toVector,
          stock.toVector
        )
        .map(x => (x.index, x.stocks))
      println(p.name(), stock.head.symbol);
      (
        p.passAnalysis(t.map(_._1), t.map(_._2)),
        stock.head.symbol
      )
    })
  )
  val filteredResult = result.map(stocks => stocks.filter(s => s._1))

  println(filteredResult.unsafeRunSync())
}

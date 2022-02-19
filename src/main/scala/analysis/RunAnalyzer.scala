package analysis

import cats.effect.IO
import cats.implicits._
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort

object RunAnalyzer extends App {

  val jdbcConfig: IO[JdbcDatabaseConfig] = JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] = jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)
    val allStocks = db.getAllStocks
    allStocks.flatMap(stocks => stocks.map(db.find(_)).sequence)
  })

  val result = xxxx.map(stocks => stocks.map(stock => {val r = TightStockDetector.passAnalysis(stock.toVector); println(r, stock.head.symbol); (r, stock.head.symbol)}))
  val filteredResult = result.map(stocks => stocks.filter(s => s._1 == true))

  println(filteredResult.unsafeRunSync())
}

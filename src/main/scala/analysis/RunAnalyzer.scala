package analysis

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import saver.DatabaseReadWritePort

object RunAnalyzer extends App {


  val jdbcConfig: IO[JdbcDatabaseConfig] = JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] = jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  val xxxx = ixa.flatMap(xa => {
    val db = new DatabaseReadWritePort[IO](xa)
    db.find("AAPL")
  })

  val result = xxxx.map(stocks => TightStockDetector.passAnalysis(stocks.toVector))

  println(result.unsafeRunSync())
}

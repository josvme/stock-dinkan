package saver


import cats.Monad
import doobie._
import doobie.implicits._
import cats.effect.{IO, Sync}
import doobie.util.transactor.Transactor.Aux
import migrations.JdbcDatabaseConfig
import models.DayData
import cats.implicits._

object DatabaseReadWritePort {
  def buildTransactor(jdbcConfig: JdbcDatabaseConfig): Aux[IO, Unit] = {

    val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
      jdbcConfig.driver, jdbcConfig.url, jdbcConfig.user.orNull, jdbcConfig.password.orNull)

    xa
  }
}


class DatabaseReadWritePort[F[+_] : Monad : Sync](xa: Transactor.Aux[F, Unit]) {

  def find(symbol: String): F[Option[DayData]] = {
    val t: doobie.ConnectionIO[Option[DayData]] = sql"select * from dayvalues where symbol = $symbol".query[DayData].option
    t.transact(xa)
  }

  def writeDayData(d: DayData): F[Option[Int]] = {
    val q = sql"insert into dayvalues(symbol, stime, sopen, sclose, low, high, volume, trade_count, vwap) values (${d.symbol}, ${d.stime}, ${d.sopen}, ${d.sclose}, ${d.low}, ${d.high}, ${d.volume}, ${d.trade_count}, ${d.vwap}) ON CONFLICT DO NOTHING"
    println(q.query.sql)
    println(d)
    val qq = q.update.run.transact(xa)
    qq.map(Option(_))
  }
}

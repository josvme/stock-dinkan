package saver

import cats.Monad
import cats.effect.{IO, Sync}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import io.circe.Json
import migrations.JdbcDatabaseConfig
import models.DayData

object DatabaseReadWritePort {
  def buildTransactor(jdbcConfig: JdbcDatabaseConfig): Aux[IO, Unit] = {

    val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
      jdbcConfig.driver,
      jdbcConfig.url,
      jdbcConfig.user.orNull,
      jdbcConfig.password.orNull
    )

    xa
  }
}

class DatabaseReadWritePort[F[+_]: Monad: Sync](xa: Transactor.Aux[F, Unit]) {
  // 1628886400000 => Fri Aug 13 2021 20:26:40 GMT+0000
  // lets take values only from then
  def find(symbol: String): F[List[DayData]] = {
    val t =
      sql"select * from dayvalues where symbol = $symbol AND stime > 1628886400000"
        .query[DayData]
        .to[List]
    t.transact(xa)
  }

  // Get only stocks where price > 10
  def getAllStocks: F[List[String]] = {
    val t =
      sql"select DISTINCT symbol from dayvalues WHERE high > 10"
        .query[String]
        .to[List]
    t.transact(xa)
  }

  def writeDayData(d: DayData): F[Option[Int]] = {
    val q =
      sql"insert into dayvalues(symbol, stime, sopen, sclose, low, high, volume, trade_count, vwap) values (${d.symbol}, ${d.stime}, ${d.sopen}, ${d.sclose}, ${d.low}, ${d.high}, ${d.volume}, ${d.trade_count}, ${d.vwap}) ON CONFLICT DO NOTHING"
    println(q.query.sql)
    println(d)
    val qq = q.update.run.transact(xa)
    qq.map(Option(_))
  }

  def writeFundamentals(symbol: String, data: Json): F[Option[Int]] = {
    import doobie.postgres.circe.jsonb.implicits._
    implicit val meta: Meta[Json] = new Meta(pgDecoderGet, pgEncoderPut)
    val q =
      sql"insert into fundamentals(symbol, data) values (${symbol}, $data) ON CONFLICT DO NOTHING"
    val qq = q.update.run.transact(xa)
    qq.map(Option(_))
  }
}

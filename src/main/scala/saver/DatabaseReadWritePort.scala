package saver

import cats.Monad
import cats.effect.{Async, Resource}
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import io.circe.Json
import migrations.JdbcDatabaseConfig
import models.DayData

import cats.effect.unsafe.implicits.global

object DatabaseReadWritePort {
  def buildTransactor[F[_]: Async](
      jdbcConfig: JdbcDatabaseConfig
  ): Resource[F, HikariTransactor[F]] = {
    // Resource yielding a transactor configured with a bounded connect EC and an unbounded
    // transaction EC. Everything will be closed and shut down cleanly after use.
    val transactor: Resource[F, HikariTransactor[F]] =
      for {
        ce <- ExecutionContexts.fixedThreadPool[F](4) // our connect EC
        xa <- HikariTransactor.newHikariTransactor[F](
          jdbcConfig.driver, // driver classname
          jdbcConfig.url, // connect URL
          jdbcConfig.user.orNull, // username
          jdbcConfig.password.orNull, // password
          ce // await connection here
        )
      } yield xa

    transactor
  }
}

class DatabaseReadWritePort[F[+_]: Monad: Async](
    xa: Resource[F, HikariTransactor[F]]
) {
  // 1628886400000 => Fri Aug 13 2021 20:26:40 GMT+0000
  // lets take values only from then
  def find(symbol: String): F[List[DayData]] = {
    val t =
      sql"select * from dayvalues where symbol = $symbol AND stime > 1628886400 order by stime desc"
        .query[DayData]
        .to[List]
    xa.use(x => t.transact(x))
  }

  // Get only stocks where price > 10
  def getAllStocksWithPriceMoreThanTen: F[List[String]] = {
    val t =
      sql"select DISTINCT symbol from dayvalues WHERE high > 10 order by symbol"
        .query[String]
        .to[List]

    xa.use(x => t.transact(x))
  }

  def getAllStocks: F[List[String]] = {
    val t =
      sql"select DISTINCT symbol from dayvalues order by symbol"
        .query[String]
        .to[List]

    xa.use(x => t.transact(x))
  }

  def getLatestPointForStock(symbol: String): F[Long] = {
    // Wed Feb 26 2020 13:19:25 GMT+0000 is 1582723165
    val t =
      sql"select coalesce(max(stime), 1582723165) from dayvalues where symbol = $symbol"
        .query[Long]
        .unique

    val k = xa.use(x => t.transact(x))
    k
  }

  def writeDayData(d: DayData): F[Option[Int]] = {
    val q =
      sql"insert into dayvalues(symbol, stime, sopen, sclose, low, high, volume, trade_count, vwap) values (${d.symbol}, ${d.stime}, ${d.sopen}, ${d.sclose}, ${d.low}, ${d.high}, ${d.volume}, ${d.trade_count}, ${d.vwap}) ON CONFLICT DO NOTHING"
    println(q.query.sql)
    println(d)

    val qq = xa.use { x => q.update.run.transact(x) }
    qq.map(Option(_))
  }

  def writeDayDataList(d: List[DayData]): F[Option[Int]] = {
    val q =
      "insert into dayvalues(symbol, stime, sopen, sclose, low, high, volume, trade_count, vwap) values (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"
    val numberOfRows = {
      Update[(String, Long, Double, Double, Double, Double, Long, Int, Double)](
        q
      ).updateMany(
        d.map(d =>
          (
            d.symbol,
            d.stime,
            d.sopen,
            d.sclose,
            d.low,
            d.high,
            d.volume,
            d.trade_count,
            d.vwap
          )
        )
      )
    }
    val qq = xa.use { x => numberOfRows.transact(x) }
    println(d.headOption)
    qq.map(Option(_))
  }

  def writeFundamentals(symbol: String, data: Json): F[Option[Int]] = {
    import doobie.postgres.circe.jsonb.implicits._
    implicit val meta: Meta[Json] = new Meta(pgDecoderGet, pgEncoderPut)
    val q =
      sql"insert into fundamentals(symbol, data) values (${symbol}, $data) ON CONFLICT (symbol) DO UPDATE SET data = ${data}"
    val qq = xa.use { x => q.update.run.transact(x) }
    qq.map(Option(_))
  }
}

package utilies

import cats.Monad
import cats.implicits._
import cats.effect.{Async, Resource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.query.Query0

class Fundamentals[F[+_]: Monad: Async](
    dbHandle: F[Resource[F, HikariTransactor[F]]]
) {
  // Get only stocks where price > 10
  def getBlankCheckCompanies: F[List[String]] = {
    val t = Query0[String](
      "select symbol from fundamentals where jsonb_path_exists(data, '$ ? ((@.fullTimeEmployees != 0) && (@.ebitda != 0))') order by symbol"
    ).to[List]

    dbHandle.flatMap(xa => xa.use(x => t.transact(x)))
  }
}

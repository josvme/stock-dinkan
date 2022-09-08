package migrations

import cats.effect.Sync

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: String,
    password: String,
    migrationsTable: String,
    migrationsLocations: List[String]
)

object JdbcDatabaseConfig {
  def loadFromGlobal[F[_]: Sync](
      environment: String
  ): F[JdbcDatabaseConfig] =
    Sync[F].delay {
      val driver = "org.postgresql.Driver"
      val host = "127.0.0.1"
      val port = 5432
      val dbName = "postgres"
      val url = s"jdbc:postgresql://$host:$port/$dbName"
      val user = "postgres"
      val password = "postgres"
      val migrationsTable = "FlywaySchemaHistory"
      val migrationsLocations = List(
        "classpath:stockdinkan/jdbc"
      )
      JdbcDatabaseConfig(
        url,
        driver,
        user,
        password,
        migrationsTable,
        migrationsLocations
      )
    }

}

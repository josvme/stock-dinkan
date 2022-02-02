package migrations

import cats.implicits._
import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import com.typesafe.scalalogging.LazyLogging

object DBMigrationsCommand extends IOApp with LazyLogging {
  /**
   * Lists all JDBC data-sources, defined in `application.conf`
   */
  val dbConfigNamespaces = List(
    "stockdinkan.jdbc"
  )

  def run(args: List[String]): IO[ExitCode] = {
    val migrate =
      dbConfigNamespaces.traverse_ { namespace =>
        for {
          _   <- IO(logger.info(s"Migrating database configuration: $namespace"))
          cfg <- JdbcDatabaseConfig.loadFromGlobal[IO](namespace)
          _   <- DBMigrations.migrate[IO](cfg)
        } yield ()
      }
    migrate.as(ExitCode.Success)
  }
}


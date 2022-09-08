package migrations

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object DBMigrationsCommand extends IOApp {

  /** Lists all JDBC data-sources, defined in `application.conf`
    */
  val dbConfigNamespaces = List(
    "stockdinkan.jdbc"
  )

  def run(args: List[String]): IO[ExitCode] = {
    val migrate =
      dbConfigNamespaces.traverse_ { namespace =>
        for {
          _ <- IO(println(s"Migrating database configuration: $namespace"))
          cfg <- JdbcDatabaseConfig.loadFromGlobal[IO](namespace)
          _ <- DBMigrations.migrate[IO](cfg)
        } yield ()
      }
    migrate.as(ExitCode.Success)
  }
}

package migrations

import cats.effect.Sync
import com.typesafe.scalalogging.LazyLogging
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.Location
import org.flywaydb.core.Flyway
import scala.jdk.CollectionConverters._

object DBMigrations extends LazyLogging {

  def migrate[F[_]: Sync](config: JdbcDatabaseConfig): F[Int] =
    Sync[F].delay {
      logger.info(
        "Running migrations from locations: " +
          config.migrationsLocations.mkString(", ")
      )
      val count = unsafeMigrate(config)
      logger.info(s"Executed $count migrations")
      count
    }

  private def unsafeMigrate(config: JdbcDatabaseConfig): Int = {
    val m: FluentConfiguration = Flyway.configure
      .dataSource(
        config.url,
        config.user.orNull,
        config.password.orNull
      )
      .group(true)
      .outOfOrder(false)
      .table(config.migrationsTable)
      .locations(
        config.migrationsLocations
          .map(new Location(_)): _*
      )
      .baselineOnMigrate(true)

    logValidationErrorsIfAny(m)
    m.load().migrate().migrationsExecuted
  }

  private def logValidationErrorsIfAny(m: FluentConfiguration): Unit = {
    val validated = m.ignorePendingMigrations(true)
      .load()
      .validateWithResult()

    if (!validated.validationSuccessful)
      for (error <- validated.invalidMigrations.asScala)
        logger.warn(s"""
                       |Failed validation:
                       |  - version: ${error.version}
                       |  - path: ${error.filepath}
                       |  - description: ${error.description}
                       |  - errorCode: ${error.errorDetails.errorCode}
                       |  - errorMessage: ${error.errorDetails.errorMessage}
        """.stripMargin.strip)
  }
}


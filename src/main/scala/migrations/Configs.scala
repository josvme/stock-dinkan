package migrations

import cats.effect.Sync
import com.typesafe.config.ConfigFactory
import pureconfig.generic.semiauto._
import pureconfig.{ConfigConvert, ConfigSource}

final case class JdbcDatabaseConfig(
    url: String,
    driver: String,
    user: Option[String],
    password: Option[String],
    migrationsTable: String,
    migrationsLocations: List[String]
)

object JdbcDatabaseConfig {
  def loadFromGlobal[F[_]: Sync](
      configNamespace: String
  ): F[JdbcDatabaseConfig] =
    Sync[F].delay {
      val config = ConfigFactory.load()
      ConfigSource.fromConfig(config.getConfig(configNamespace)).loadOrThrow
    }

  // Integration with PureConfig
  implicit val configConvert: ConfigConvert[JdbcDatabaseConfig] =
    deriveConvert
}

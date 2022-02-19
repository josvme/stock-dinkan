import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import filesources.DataSource
import migrations.JdbcDatabaseConfig
import saver.{DatabaseReadWritePort, DummyDayDataProvider}

object Main extends App {
  println("Welcome to Stock Dinkan")

  val files = DataSource.getAllStockFileNames[IO]
  val jdbcConfig: IO[JdbcDatabaseConfig] =
    JdbcDatabaseConfig.loadFromGlobal[IO]("stockdinkan.jdbc")
  val ixa: IO[Aux[IO, Unit]] =
    jdbcConfig.map(jdbc => DatabaseReadWritePort.buildTransactor(jdbc))

  def getStockName(getName: String) = {
    getName.takeWhile(x => x != '.')
  }

  ixa
    .flatMap(xa => {
      val port = new DatabaseReadWritePort[IO](xa)
      val insert = port.writeDayData(
        DummyDayDataProvider.generateDummyData(20).drop(5).head
      )
      val stocks = port.find("AAPL")
      insert >> stocks
    })
    .map(println)
    .unsafeRunSync()
}

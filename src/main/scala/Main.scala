import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import filesources.DataSource
import cats.implicits._

import scala.io.{BufferedSource, Source}

object Main extends App {
  println("Welcome to Stock Dinkan")

  val files = DataSource.getAllStockFileNames[IO]

  def getStockName(getName: String) = {
    getName.takeWhile(x => x != '.')
  }

  //val fullStocks = files.flatMap(file => {
  //  val stockValues = for {
  //    name <- file

  //    val acquire: IO[BufferedSource] = IO(Source.fromFile(name))
  //    val quotes = Resource.fromAutoCloseable(acquire)
  //      .use { source =>
  //        IO {
  //          val lines = (for (line <- source.getLines) yield line).mkString
  //          val parsedJson = JsonReader.parseFile(lines).toOption
  //          parsedJson.map(q => StockFile(getStockName(name.getName), q))
  //        }
  //      }
  //    val stockQuotes = quotes.map(jsonFile => DataPointCreator.createDayDataPoints(jsonFile))
  //  } yield stockQuotes

  // stockValues.toList.sequence
  //}
  //)

  //  println (fullStocks.unsafeRunSync())
}
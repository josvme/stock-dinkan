import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import models.StockFile
import transformers.{DataPointCreator, JsonReader}

import scala.io.{BufferedSource, Source}

object Main extends App {
  println("Welcome to Stock Dinkan")

  val stock = "AAPL"
  val name = s"./${stock}.json"
  val acquire: IO[BufferedSource] = IO(Source.fromFile(name))
  val quotes = Resource.fromAutoCloseable(acquire)
    .use { source =>
      IO {
        val lines = (for (line <- source.getLines) yield line).mkString
        val parsedJson = JsonReader.parseFile(lines).toOption
        parsedJson.map(q => StockFile(stock, q))
      }
    }
  val stockQuotes = quotes.map(jsonFile => DataPointCreator.createDayDataPoints(jsonFile))

  println(stockQuotes.unsafeRunSync())
}
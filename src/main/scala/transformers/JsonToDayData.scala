package transformers

import io.circe._
import io.circe.parser._
import models.DayData

object JsonToDayData {
  def parseJson(s: String, symbol: String): List[DayData] = {
    val t = parse(s).toOption
    val k = for {
      x <- t
      y <- x.hcursor.downField("chart").get[Vector[Json]]("result").toOption
    } yield y

    val kk = k.getOrElse(Vector[Json]()).map(j => jsonToDayData(j, symbol))
    kk.head.toList
  }

  def getValuesDouble(
      pattern: String,
      json: Json
  ): Vector[Double] = {
    json.hcursor
      .get[Vector[Json]](pattern)
      .toOption
      .getOrElse(Vector[Json]())
      .map(_.as[Double].getOrElse(0.0))
  }

  def jsonToDayData(json: Json, symbol: String): Vector[DayData] = {
    val sstime = json.hcursor
      .get[Vector[Json]]("timestamp")
      .toOption
      .getOrElse(Vector[Json]())

    val stime =
      sstime.map(t => t.as[Long].getOrElse(9L))

    val indicators = json.hcursor
      .downField("indicators")
      .get[Vector[Json]]("quote")
      .toOption
      .getOrElse(Vector[Json]())
      .head

    val low = indicators.hcursor
      .get[Vector[Json]]("low")
      .toOption
      .getOrElse(Vector[Json]())
      .map(_.as[Double].getOrElse(0.0))

    val sopen = getValuesDouble("open", indicators)

    val sclose = getValuesDouble("close", indicators)

    val high = getValuesDouble("high", indicators)

    val volume = indicators.hcursor
      .get[Vector[Json]]("volume")
      .toOption
      .getOrElse(Vector[Json]())
      .map(_.as[Int].getOrElse(0))

    val combined = stime
      .zip(sopen)
      .zip(sclose)
      .zip(low)
      .zip(high)
      .zip(volume)
      .map { case (((((t, o), c), l), h), v) =>
        DayData(0, symbol, t, o, c, l, h, v, 0, 0.0)
      }

    combined
  }
}

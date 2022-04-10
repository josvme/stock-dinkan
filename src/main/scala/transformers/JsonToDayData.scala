package transformers

import io.circe._
import io.circe.optics.JsonPath._
import io.circe.parser._
import models.DayData
import monocle.Optional

object JsonToDayData {
  def parseJson(s: String, symbol: String): List[DayData] = {
    val t = parse(s).toOption
    val _bars = root.chart.result.arr
    val k = for {
      x <- t
      y <- _bars.getOption(x)
    } yield y

    val kk = k.getOrElse(Vector[Json]()).map(j => jsonToDayData(j, symbol))
    kk.head.toList
  }

  def getValuesDouble(
      pattern: Optional[Json, Vector[Json]],
      json: Json
  ): Vector[Double] = {
    pattern
      .getOption(json)
      .getOrElse(Vector[Json]())
      .map(_.as[Double].getOrElse(0.0))
  }

  def getValuesInt(
      pattern: Optional[Json, Vector[Json]],
      json: Json
  ): Vector[Int] = {
    pattern
      .getOption(json)
      .getOrElse(Vector[Json]())
      .map(_.as[Int].getOrElse(0))
  }

  def jsonToDayData(json: Json, symbol: String): Vector[DayData] = {
    val _stime: Optional[Json, Vector[Json]] = root.timestamp.arr
    val sstime = _stime
      .getOption(json)
      .getOrElse(Vector[Json]())

    val stime =
      sstime.map(t => t.as[Long].getOrElse(9L))

    val _indicators = root.indicators.quote.arr
    val indicators = _indicators
      .getOption(json)
      .getOrElse(Vector[Json]())
      .head

    val _low = root.low.arr
    val low = _low
      .getOption(indicators)
      .getOrElse(Vector[Json]())
      .map(_.as[Double].getOrElse(0.0))

    val _sopen = root.open.arr
    val sopen = getValuesDouble(_sopen, indicators)

    val _sclose = root.close.arr
    val sclose = getValuesDouble(_sclose, indicators)

    val _high = root.high.arr
    val high = getValuesDouble(_high, indicators)

    val _volume = root.volume.arr
    val volume = getValuesInt(_volume, indicators)

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

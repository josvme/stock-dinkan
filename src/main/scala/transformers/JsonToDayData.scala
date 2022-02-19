package transformers

import io.circe._
import io.circe.optics.JsonPath._
import io.circe.parser._
import models.DayData

import java.time.Instant

object JsonToDayData {
  def parseJson(s: String, symbol: String): List[DayData] = {
    val t = parse(s).toOption
    val _bars = root.bars.arr
    val k = for {
      x <- t
      y <- _bars.getOption(x)
    } yield y

    val kk = k.getOrElse(Vector[Json]()).map(j => jsonToDayData(j, symbol))
    kk.toList
  }

  def jsonToDayData(json: Json, symbol: String): DayData = {

    val _stime = root.t.string
    val stime = Instant
      .parse(_stime.getOption(json).getOrElse("2020-01-06T05:00:00Z"))
      .toEpochMilli

    val _sopen = root.o.double
    val sopen = _sopen.getOption(json).getOrElse(0.0)

    val _sclose = root.c.double
    val sclose = _sclose.getOption(json).getOrElse(0.0)

    val _low = root.l.double
    val low = _low.getOption(json).getOrElse(0.0)

    val _high = root.h.double
    val high = _high.getOption(json).getOrElse(0.0)

    val _volume = root.v.int
    val volume = _volume.getOption(json).getOrElse(0)

    val _trade_count = root.n.int
    val trade_count = _trade_count.getOption(json).getOrElse(0)

    val _vwap = root.vw.double
    val vwap = _vwap.getOption(json).getOrElse(0.0)

    DayData(
      0,
      symbol,
      stime,
      sopen,
      sclose,
      low,
      high,
      volume,
      trade_count,
      vwap
    )
  }
}

package transformers

import cats.Functor
import models.{DayData, StockFile, StockQuotes}

object DataPointCreator {

  def createDayDataPoints[F[_] : Functor](inp: F[StockFile]): F[StockQuotes] = {
    Functor[F].map(inp) { q =>

      val quotes = {
        val i = q.json
        val timestamps = i.low.keys
        val dayData = timestamps.map(timestamp => {
          for {
            o <- i.open.get(timestamp)
            c <- i.close.get(timestamp)
            l <- i.low.get(timestamp)
            h <- i.high.get(timestamp)
            v <- i.volume.get(timestamp)
            t <- i.trade_count.get(timestamp)
            vw <- i.vwap.get(timestamp)
          } yield DayData(timestamp / 1000, o, c, l, h, v, t, vw)
        }).toVector

        val dayDataFiltered = dayData.foldRight(Vector[DayData]())((n, e) => n match {
          case Some(value) => e.appended(value)
          case None => e
        })

        dayDataFiltered
      }

      StockQuotes(q.symbol, quotes)
    }

  }
}

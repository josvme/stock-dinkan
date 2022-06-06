package utilies

import models.{
  DayData,
  DayDataExtras,
  TimeBasedStockCompareModel,
  WeekData,
  WeekDataExtras
}

import scala.collection.mutable

object Indicators {
  def getExtendedDayDataFromDayData(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Vector[DayDataExtras] = {
    val drs = computeDailyRS(index, stocks)
    val sma10 = computeDailySimpleMovingAverage(stocks.map(_.sclose), 10)
    val sma50 = computeDailySimpleMovingAverage(stocks.map(_.sclose), 50)
    val sma200 = computeDailySimpleMovingAverage(stocks.map(_.sclose), 200)

    (stocks zip drs zip sma10 zip sma50 zip sma200).map {
      case ((((stock, rs), sma10), sma50), sma200) =>
        DayDataExtras(stock, rs, sma10, sma50, sma200)
    }
  }

  def getExtendedWeekDataFromDayData(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Vector[WeekDataExtras] = {
    val drs = computeWeeklyRS(index, stocks)
    val weeklyData = computeWeeklyFromDailyData(stocks)
    val sma10 = computeDailySimpleMovingAverage(weeklyData.map(_.sclose), 10)
    val sma50 = computeDailySimpleMovingAverage(weeklyData.map(_.sclose), 50)

    (weeklyData zip drs zip sma10 zip sma50).map {
      case (((week, rs), sma10), sma50) =>
        WeekDataExtras(week, rs, sma10, sma50)
    }
  }

  def computeDailyRS(
      index: Vector[DayData],
      stock: Vector[DayData]
  ): Vector[Double] = computeRS(
    index.map(x => (x.sopen, x.sclose)),
    stock.map(x => (x.sopen, x.sclose))
  )

  def computeWeeklyRS(
      index: Vector[DayData],
      stock: Vector[DayData]
  ): Vector[Double] = {
    val weeklyIndexData = computeWeeklyFromDailyData(index)
    val weeklyStockData = computeWeeklyFromDailyData(stock)
    computeRS(
      weeklyIndexData.map(x => (x.sopen, x.sclose)),
      weeklyStockData.map(x => (x.sopen, x.sclose))
    )
  }

  def computeRS(
      index: Vector[(Double, Double)],
      stock: Vector[(Double, Double)]
  ): Vector[Double] = {
    val indexRS = index.map({ case (o, c) => (c - o) / o })
    val stockRS = stock.map({ case (o, c) => (c - o) / o })

    (indexRS zip stockRS).map({ case (index, stock) =>
      (stock - index) / Math.abs(stock + index)
    })
  }

  def computeDailySimpleMovingAverage(
      stocks: Vector[Double],
      n: Int
  ): Vector[Double] = {
    val s = stocks.zipWithIndex
    s.map(x => {
      val endIndex = x._2
      val startIndex = math.max(endIndex - n + 1, 0)
      val elements = s.slice(startIndex, endIndex + 1)
      val avgValue = elements.foldLeft(0.0)({ case (initial, (price, index)) =>
        initial + price
      })
      avgValue / elements.length
    })
  }

  def convertDayDataToWeekData(dayData: Vector[DayData]): WeekData = {
    val firstDay = dayData.head
    val lastDay = dayData.last
    val weekLow = dayData.foldLeft(firstDay.low) { case (i, j) =>
      math.min(i, j.low)
    }
    val weekHigh = dayData.foldLeft(firstDay.high) { case (i, j) =>
      math.max(i, j.high)
    }
    val weekVolume = dayData.foldLeft(firstDay.volume) { case (i, j) =>
      i + j.volume
    }
    val weekTradeCount = dayData.foldLeft(firstDay.trade_count) { case (i, j) =>
      i + j.trade_count
    }

    WeekData(
      firstDay.id,
      firstDay.symbol,
      firstDay.stime,
      firstDay.sopen,
      lastDay.sclose,
      weekLow,
      weekHigh,
      weekVolume,
      weekTradeCount,
      firstDay.vwap
    )
  }

  def computeWeeklyFromDailyData(stocks: Vector[DayData]): Vector[WeekData] = {

    def getWeekNumberAndYearFromUnixTimestamp(timeStamp: Long): (Int, Int) = {
      import java.util.Calendar
      import java.util.GregorianCalendar

      val calendar = new GregorianCalendar()
      calendar.setTimeInMillis(timeStamp)
      val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
      val year = calendar.get(Calendar.YEAR)
      (year, weekNumber)
    }

    val stocksOnWeek = stocks.groupBy((dayData) =>
      getWeekNumberAndYearFromUnixTimestamp(dayData.stime)
    )
    // groupby gives a hashmap which is not sorted
    val result = stocksOnWeek
      .map({
        case ((year, week), dayData) => {
          (year * 100 + week, convertDayDataToWeekData(dayData))
        }
      })
      .values
      .toVector

    result.sortWith({ case (x, y) => x.stime < y.stime })
  }

  def computeDailyExponentialMovingAverage(stocks: Vector[Double], n: Int) =
    ???

  def combineIndexAndStockData(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Vector[TimeBasedStockCompareModel] = {
    val indexHash = index.foldLeft(mutable.HashMap[Long, DayData]())({
      case (o, d) =>
        o + (d.stime -> d)
    })
    val stocksHash = stocks.foldLeft(mutable.HashMap[Long, DayData]())({
      case (o, d) =>
        o + (d.stime -> d)
    })

    val combinedKeys = indexHash.keys ++ stocksHash.keys
    var combinedStockData = List[TimeBasedStockCompareModel]()
    for (key <- combinedKeys) {
      if (indexHash.contains(key) && stocksHash.contains(key)) {
        combinedStockData = combinedStockData :+ TimeBasedStockCompareModel(
          key,
          indexHash(key),
          stocksHash(key)
        )
      }
    }
    combinedStockData.toVector
  }
}

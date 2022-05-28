import datasync.SyncLatestDataWithYahooFinance
import downloader.YahooStockConfig

class SyncDataTests extends munit.FunSuite {

  /** Write tests for
    * 1. When trading is happening the data should be download only till yesterday
    * 2. When all data till today is available, dont try to download
    * 3. If ran on sunday / saturday, download day from last friday
    */
  test(
    "Should not download data from that day, when trading it not yet finished"
  ) {
    val symbol = "AAPL"
    // 1653512566 - Wed May 25 2022 21:02:46 GMT+0000
    val startTimeLong1 = 1653512566
    // 1653744566 - Sat May 28 2022 13:29:26 GMT+0000
    val endTimeLong1 = 1653744566

    // 1653512566 - Fri May 27 2022 14:43:17 GMT+0000
    val startTimeLong2 = 1653662597
    // 1653932566 - Mon May 30 2022 17:42:46 GMT+0000
    // Timing is in trading hours
    val endTimeLong2 = 1653932566
    // Mon May 30 2022 00:00:00 GMT+0000
    val expectedEndTimeLong2 = 1653868800

    // 1653512566 - Sat May 28 2022 04:36:06 GMT+0000
    val startTimeLong3 = 1653512566
    // 1653952566 - Mon May 30 2022 23:16:06 GMT+0000
    // Timing is in trading hours
    val endTimeLong3 = 1653952566
    // Mon May 30 2022 23:16:06 GMT+0000
    val expectedEndTimeLong3 = 1653952566

    // 1653512566 - Fri May 27 2022 14:43:17 GMT+0000
    val startTimeLong4 = 1653662597
    // 1653835397 - Sun May 29 2022 14:43:17 GMT+0000
    // Timing is in trading hours
    val endTimeLong4 = 1653835397
    // 1653835397 - Sun May 29 2022 14:43:17 GMT+0000
    val expectedEndTimeLong4 = 1653835397

    val inputs =
      List(
        YahooStockConfig("1d", startTimeLong1.toString, endTimeLong1.toString),
        YahooStockConfig("1d", startTimeLong2.toString, endTimeLong2.toString),
        YahooStockConfig("1d", startTimeLong3.toString, endTimeLong3.toString),
        YahooStockConfig("1d", startTimeLong4.toString, endTimeLong4.toString)
      )

    val outputConfigs =
      List(
        YahooStockConfig("1d", startTimeLong1.toString, endTimeLong1.toString),
        YahooStockConfig(
          "1d",
          startTimeLong2.toString,
          expectedEndTimeLong2.toString
        ),
        YahooStockConfig(
          "1d",
          startTimeLong3.toString,
          expectedEndTimeLong3.toString
        ),
        YahooStockConfig("1d", startTimeLong4.toString, endTimeLong4.toString)
      )

    inputs zip outputConfigs foreach { case (input, output) =>
      val (_, config) =
        SyncLatestDataWithYahooFinance.getStockDataDownloadConfig(symbol, input)
      assertEquals(config, output)
    }

    val outputBools = List(false, false, false, true)

    inputs zip outputBools foreach { case (input, output) =>
      val result =
        SyncLatestDataWithYahooFinance.dataAlreadyDownloaded(input)
      assertEquals(result, output)
    }
  }
}

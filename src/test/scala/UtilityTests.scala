import utilies.Indicators

class UtilityTests extends munit.FunSuite {
  test("Test Simple Moving Average") {
    val input = List(1.0, 2, 3, 4, 5, 6, 7, 8).toVector
    val expectedOutput = List(1.0, 1.5, 2, 3, 4, 5, 6, 7).toVector
    val output = Indicators.computeDailySimpleMovingAverage(input, 3)

    assertEquals(expectedOutput, output)
  }

  test("Test Relative Strength with Respect to Stock") {
    val index = List((1.0, 2.0), (2.0, 3.0), (3.0, 4.0), (4.0, 8.0)).toVector
    val stock = List((1.0, 2.0), (2.0, 3.0), (3.0, 2.0), (2.0, 3.0)).toVector
    val expectedOutput = List(1.0, 1.0, -1.0, .5).toVector
    val output = Indicators.computeRS(index, stock)

    assertEquals(expectedOutput, output)
  }
}

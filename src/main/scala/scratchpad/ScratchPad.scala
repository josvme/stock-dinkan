package scratchpad

object ScratchPad {
  import me.shadaj.scalapy.py
  import me.shadaj.scalapy.py.SeqConverters

  val listLengthPython = py.Dynamic.global.len(List(1, 2, 3).toPythonProxy)

  def main(args: Array[String]): Unit = {
    println(listLengthPython)

    val yfinance = py.module("yfinance")
    val msft = yfinance.Ticker("MSFT")
    println(msft.info.bracketAccess("sector"))
  }
}

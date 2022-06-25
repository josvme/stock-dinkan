package frontend

import scala.scalajs.js
import Implicits._

object Client {

  import org.scalajs.dom
  import com.raquo.laminar.api.L._

  val nameVar = Var(initial = "AAPL")

  val rootElement = div(
    input(
      onMountFocus,
      placeholder := "Enter symbol",
      inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> nameVar }
    ),
    span(
      child.text <-- nameVar.signal.map(_.toUpperCase)
    ),
    // This is a hacky solution
    span(
      child.text <-- nameVar.signal.map(x => {
        new TradingViewWidget(tradingViewParams(x.toUpperCase))
      })
    )
  )

  // In most other examples, containerNode will be set to this behind the scenes
  val containerNode = dom.document.querySelector("#container")

  val trading = nameVar.signal.map(symbol => {})

  // Is a side-effect
  // new TradingViewWidget(tradingViewParams("CFLT"))

  def main(args: Array[String]): Unit = {
    render(containerNode, rootElement)
  }

  def tradingViewParams(symbol: String) = {
    js.Dynamic.literal(
      autosize = true,
      symbol = symbol,
      interval = "D",
      timezone = "Etc/UTC",
      theme = "light",
      style = "1",
      locale = "en",
      toolbar_bg = "#f1f3f6",
      enable_publishing = false,
      allow_symbol_change = true,
      calendar = true,
      details = true,
      container_id = "tradingview_f55fd",
      studies = List(
        "MACD@tv-basicstudies",
        "MOM@tv-basicstudies",
        "MASimple@tv-basicstudies",
        "PriceVolumeTrend@tv-basicstudies"
      )
    )
  }
}

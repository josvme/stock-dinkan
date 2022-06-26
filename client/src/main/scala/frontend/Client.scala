package frontend

import scala.scalajs.js
import Implicits._
import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.scalajs.js.JSON

object Client {

  val nameVar = Var(initial = "AAPL")
  val url = "http://localhost:8080/analysis/tight-consolidation"
  val stockListVar = Var("")

  val chartElementsNode = div(
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

  private def getSymbol(
      e: ReactiveHtmlElement[org.scalajs.dom.html.LI]
  ): String = {
    println(e.ref.textContent)
    e.ref.textContent
  }

  private def renderList(s: String) = {
    val list = JSON.parse(s).asInstanceOf[js.Array[String]]
    list.toSeq.map(x =>
      li(
        x,
        inContext(thisNode => onClick.mapTo(getSymbol(thisNode)) --> nameVar)
      )
    )
  }

  val stockElementsNode = div(
    button(
      "Load tight stocks",
      inContext { thisNode =>
        val $click = thisNode.events(onClick)
        val $response = $click.flatMap { opt =>
          AjaxEventStream
            .get(
              url = url
            )
            .map(_.responseText)
            .recover { case err: AjaxStreamError => Some(err.getMessage) }
        }

        List(
          $response --> stockListVar
        )
      }
    ),
    div(
      ul(
        cls("stock-list"),
        children <-- stockListVar.signal
          .map(renderList)
      )
    )
  )

  // In most other examples, containerNode will be set to this behind the scenes
  val containerNode = dom.document.querySelector("#container")

  val stockListNode = dom.document.querySelector("#stock-list")

  // Is a side-effect
  // new TradingViewWidget(tradingViewParams("CFLT"))

  def main(args: Array[String]): Unit = {
    render(containerNode, chartElementsNode)
    render(stockListNode, stockElementsNode)
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

package frontend

import com.raquo.laminar.nodes.TextNode

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSGlobal("TradingView.widget")
class TradingViewWidget(doc: js.Dynamic) extends js.Object {}
object Implicits {

  implicit def tradingViewToTextNode(tv: TradingViewWidget): TextNode = {
    new TextNode("")
  }
}

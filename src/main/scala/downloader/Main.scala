package downloader

import cats.effect.unsafe.implicits.global

object Main extends App {
  val download = Downloader.downloadFile("AAPL")
  download.unsafeRunSync()
}

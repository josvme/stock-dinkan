package filesources

import cats.Monad
import cats.implicits._

import java.io.File

object DataSource {

  def getAllStockFundamentalFileNames[F[_]: Monad]: F[Array[File]] = {
    val fileFolder = "./stock-fundamental-files/"
    val folders: Array[File] = (new File(fileFolder)).listFiles
      .filter(s => s.getName.endsWith("json") && s.isFile)
      .sorted
    folders.pure[F]
  }
}

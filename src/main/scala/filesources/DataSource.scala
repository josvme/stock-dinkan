package filesources

import cats.Monad
import cats.implicits._

import java.io.File

object DataSource {

  def getAllStockFileNames[F[_]: Monad]: F[Array[File]] = {

    val fileFolder = "./stock-files/"
    val folders: Array[File] = (new File(fileFolder)).listFiles
      .filter(s => s.getName.endsWith("json") && s.isFile)
    folders.pure[F]
  }
}

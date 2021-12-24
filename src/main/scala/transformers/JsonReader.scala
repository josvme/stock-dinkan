package transformers

import io.circe._
import io.circe.generic.semiauto._
import models.{JsonFile}

object JsonReader {
  def parseFile(jsonData: String): Either[Error, JsonFile] = {
    parser.decode[JsonFile](jsonData)
  }

  implicit val jsonDecoder: Decoder[JsonFile] = deriveDecoder
  implicit val jsonEncoder: Encoder[JsonFile] = deriveEncoder
}


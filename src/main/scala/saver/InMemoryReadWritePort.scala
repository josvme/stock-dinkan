package saver
import cats.Monad
import cats.implicits._
import models.DayData

class InMemoryReadWritePort[F[+_]: Monad] extends WriteToSink[F] with ReadFromSink[F] {

  var internalMemory: List[DayData] = DummyDayDataProvider.generateDummyData(20)
  def writeDayData(d: DayData): F[Option[Result]] = {
    Option(Success("Wrote data")).pure[F]
  }

  def read(): F[Option[DayData]] = {
    val out = internalMemory.headOption
    out match {
      case Some(_) => {
        internalMemory = internalMemory.drop(1)
      }
      case None => None
    }
    out.pure[F]
  }
}

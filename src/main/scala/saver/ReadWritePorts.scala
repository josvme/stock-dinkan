package saver

import models.DayData

sealed trait Result

case class Success(msg: String) extends Result

case class Failure(msg: String) extends Result

trait WriteToSink[F[_]] {
  def writeDayData(d: DayData): F[Option[Result]]
}

trait ReadFromSink[F[_]] {
  def read(): F[Option[DayData]]
}

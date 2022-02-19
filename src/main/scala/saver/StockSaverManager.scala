package saver

import cats.Monad
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object StockSaverManager extends IOApp {

  val manager = new InMemoryReadWritePort[IO]()
  def run(args: List[String]): IO[ExitCode] = {
    val prog = readWriteStockData[IO](manager)
    readAndWriteUntilEnd(prog, manager).unsafeRunSync()
    IO.pure(ExitCode.Success)
  }

  def readAndWriteUntilEnd[F[+_]: Monad](
      io: F[Option[Result]],
      manager: InMemoryReadWritePort[F]
  ): F[Result] = {
    io.flatMap {
      case Some(msg) => {
        val t: F[Option[Result]] = readWriteStockData[F](manager).map(x => {
          println(x); x
        })
        readAndWriteUntilEnd(t, manager)
      }
      case None => Failure("failed").pure[F]
    }
  }

  def readWriteStockData[F[+_]: Monad](
      manager: InMemoryReadWritePort[F]
  ): F[Option[Result]] = {

    val result: OptionT[F, Result] = for {
      x <- OptionT(manager.read())
      y <- OptionT(manager.writeDayData(x))
    } yield y

    result.value
  }
}

package server

import analysis.{RunAnalyzer, TightStockDetector}
import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.middleware.CORS

import scala.concurrent.duration.DurationInt

object Server extends IOApp {

  def analysisRoutes[F[_]: Monad] = {
    val dsl = Http4sDsl[F]
    import dsl._
    val routes = HttpRoutes.of[F] {
      case GET -> Root / "analysis" / "tight-consolidation" => {
        val analysis = TightStockDetector
        val results = RunAnalyzer
          .runAndGetAnalysisResults(analysis)
          .compile
          .toList
          .unsafeRunSync()
          .filter(_.isDefined)
          .map(_.get)

        //val results = List("AAPL", "MSFT", "A", "B", "C", "D")

        Ok(results.asJson)
      }
    }
    CORS.policy.withAllowOriginAll(routes)
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withIdleTimeout(900.second)
      .withResponseHeaderTimeout(900.second)
      .withHttpApp(analysisRoutes)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}

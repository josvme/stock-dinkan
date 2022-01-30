import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion := "2.13.7"

name := "stock-dinkan"
organization := "me.josv"
version := "1.0"


val circeVersion = "0.14.1"
val circeDeps = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

val doobie = Seq(

    // Start with this one
    "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",

    // And add any of these as needed
    "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1",          // Postgres driver 42.3.1 + type mappings.
    "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC1" % "test", // Specs2 support for typechecking statements.
    "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test"  // ScalaTest support for typechecking statements.
  )

val commonSettings = Seq(
  organization := "me.josv",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.2.9",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.2.9",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.2.9",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.3.0" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.6" % Test,
    "com.softwaremill.sttp.client3" %% "core" % "3.4.1",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % "3.4.1",
  ) ++ circeDeps ++ doobie,

)

lazy val root = (project in file(".")).
  settings(commonSettings)
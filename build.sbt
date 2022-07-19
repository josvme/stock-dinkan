import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion := "2.13.7"

name := "stock-dinkan"
organization := "me.josv"
version := "1.0"

lazy val runMigrate = taskKey[Unit]("Migrates the database schema.")
lazy val root = (project in file("."))
  .settings(
    fullRunTask(runMigrate, Compile, "migrations.DBMigrationsCommand"),
    runMigrate / fork := true
  )
  .settings(commonSettings)
val fs2Version = "3.2.0"
val fs2 = Seq(
  "co.fs2" %% "fs2-core",
  "co.fs2" %% "fs2-io"
).map(_ % fs2Version)
val circeVersion = "0.14.1"
val circeDeps = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)
val doobie = Seq(
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1", // Postgres driver 42.3.1 + type mappings.
  "org.tpolecat" %% "doobie-postgres-circe" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC1" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test" // ScalaTest support for typechecking statements.
)

val Http4sVersion = "1.0.0-M33"
val http4s = Seq(
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion
)

addCommandAlias("run-db-migrations", "runMigrate")
val commonSettings = Seq(
  organization := "me.josv",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.4",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.4",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.4",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.3.0" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    "com.softwaremill.sttp.client3" %% "core" % "3.4.1",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % "3.4.1",
    // migrations
    "com.github.pureconfig" %% "pureconfig" % "0.17.1",
    "org.flywaydb" % "flyway-core" % "8.4.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    // for python interoperability
    "me.shadaj" %% "scalapy-core" % "0.5.1"
  ) ++ circeDeps ++ doobie ++ fs2 ++ http4s
)

// Load python
fork := true
import ai.kien.python.Python

// This should take care of virtualenv
lazy val python = Python(
  "/home/josv/Projects/StockDinkan/stock-dinkan/env/bin/python"
)
lazy val javaOpts = python.scalapyProperties.get.map { case (k, v) =>
  s"""-D$k=$v"""
}.toSeq

javaOptions ++= javaOpts

import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion := "3.3.0"

name := "stock-dinkan"
organization := "me.josv"
version := "1.0"

lazy val runMigrate = taskKey[Unit]("Migrates the database schema.")
val LogbackVersion = "1.4.0"
lazy val root = (project in file("."))
  .settings(
    fullRunTask(runMigrate, Compile, "migrations.DBMigrationsCommand"),
    runMigrate / fork := true
  )
  .settings(commonSettings)
val fs2Version = "3.2.13"
val fs2 = Seq(
  "co.fs2" %% "fs2-core",
  "co.fs2" %% "fs2-io"
).map(_ % fs2Version)
val circeVersion = "0.14.1"
val circeDeps = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)
val doobie = Seq(
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2", // Postgres driver 42.3.1 + type mappings.
  "org.tpolecat" %% "doobie-postgres-circe" % "1.0.0-RC2",
  // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC2" % "test" // ScalaTest support for typechecking statements.
)

val Http4sVersion = "1.0.0-M36"
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
    "org.typelevel" %% "cats-effect" % "3.3.12",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.12",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.3.0" % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    "com.softwaremill.sttp.client3" %% "core" % "3.7.6",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % "3.7.6",
    // migrations
    "org.flywaydb" % "flyway-core" % "8.4.2",
    // for python interoperability
    "me.shadaj" %% "scalapy-core" % "0.5.2"
  ) ++ circeDeps ++ doobie ++ fs2 ++ http4s
)

// Load python
fork := true
import ai.kien.python.Python

// This should take care of virtualenv
lazy val python = Python(
  "/home/josv/Projects/stock-dinkan/env/bin/python"
)
lazy val javaOpts = python.scalapyProperties.get.map { case (k, v) =>
  s"""-D$k=$v"""
}.toSeq

javaOptions ++= javaOpts

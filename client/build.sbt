enablePlugins(ScalaJSPlugin)

name := "Scala.js Frontend Client.scala"
scalaVersion := "2.13.7" // or any other Scala version >= 2.11.12

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

lazy val client = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.14.2" // Requires Scala.js >= 1.7.1
    )
  )

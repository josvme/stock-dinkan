addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
// Doesn't work with scala3
// addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

libraryDependencies += "ai.kien" %% "python-native-libs" % "0.2.2"

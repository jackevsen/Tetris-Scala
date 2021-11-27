lazy val tetris =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .settings( // Normal SBT settings
      name := "tetris",
      version := "0.0.1",
      scalaVersion := "3.1.0",
      organization := "tetris",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework")
    )
    .settings( // Indigo specific settings
      showCursor := false,
      title := "Tetris",
      gameAssetsDirectory := "assets",
      windowStartWidth := 550,
      windowStartHeight := 400,
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "indigo-json-circe" % "0.10.0",
        "io.indigoengine" %%% "indigo"            % "0.10.0",
        "io.indigoengine" %%% "indigo-extras"     % "0.10.0"
      )
    )

addCommandAlias("buildGame", ";compile;fastOptJS;indigoBuild")
addCommandAlias("runGame", ";compile;fastOptJS;indigoRun")

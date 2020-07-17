name := "MidiBridge"

lazy val osName: String =
  if (scala.util.Properties.isLinux) "linux"
  else if (scala.util.Properties.isMac) "mac"
  else if (scala.util.Properties.isWin) "win"
  else throw new Exception("Unknown platform!")

lazy val settings = Seq(
  version := "0.2.0",

  scalaVersion := "2.13.3",

  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "2.1.1",
    "io.monix" %% "monix" % "3.2.2",
    "org.openjfx" % "javafx-base" % "14.0.1" classifier osName,
    "org.openjfx" % "javafx-controls" % "14.0.1" classifier osName,
    "org.openjfx" % "javafx-graphics" % "14.0.1" classifier osName,
    "org.openjfx" % "javafx-media" % "14.0.1" classifier osName,
    "org.scalafx" %% "scalafx" % "14-R19",
    "com.miglayout" % "miglayout-javafx" % "5.2"
  ),

  mainClass in Compile := Some("org.lolhens.midibridge.Main"),

  assemblyOption in assembly := (assemblyOption in assembly).value
    .copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),

  assemblyJarName in assembly := s"${name.value}-${version.value}.sh.bat"
)

def universalScript(shellCommands: String,
                    cmdCommands: String,
                    shebang: Boolean): String = {
  Seq(
    if (shebang) "#!/usr/bin/env sh" else "",
    "@ 2>/dev/null # 2>nul & echo off & goto BOF\r",
    ":",
    shellCommands.replaceAll("\r\n|\n", "\n"),
    "exit",
    Seq(
      "",
      ":BOF",
      cmdCommands.replaceAll("\r\n|\n", "\r\n"),
      "exit /B %errorlevel%",
      ""
    ).mkString("\r\n")
  ).filterNot(_.isEmpty).mkString("\n")
}

def defaultUniversalScript(javaOpts: Seq[String] = Seq.empty,
                           shebang: Boolean): Seq[String] = {
  val javaOptsString = javaOpts.map(_ + " ").mkString
  Seq(universalScript(
    shellCommands = s"""exec java -jar $javaOptsString$$JAVA_OPTS "$$0" "$$@"""",
    cmdCommands = s"""java -jar $javaOptsString%JAVA_OPTS% "%~dpnx0" %*""",
    shebang = shebang
  ))
}

lazy val root = project.in(file("."))
  .enablePlugins(
    JavaAppPackaging,
    UniversalPlugin,
    ClasspathJarPlugin
  )
  .settings(settings: _*)

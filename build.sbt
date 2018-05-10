name := "MidiBridge"

lazy val settings = Seq(
  version := "0.1.0",

  scalaVersion := "2.12.4",

  resolvers ++= Seq(
    "lolhens-maven" at "http://artifactory.lolhens.de/artifactory/maven-public/",
    Resolver.url("lolhens-ivy", url("http://artifactory.lolhens.de/artifactory/ivy-public/"))(Resolver.ivyStylePatterns)
  ),

  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.typelevel" %% "cats-core" % "1.1.0",
    "io.monix" %% "monix" % "3.0.0-RC1",
    "com.oracle" % "jfxrt" % "8",
    "org.scalafx" %% "scalafx" % "8.0.144-R12",
    "com.miglayout" % "miglayout-javafx" % "5.1"
  ),

  mainClass in Compile := Some("org.lolhens.midibridge.Main"),

  assemblyOption in assembly := (assemblyOption in assembly).value
    .copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),

  assemblyJarName in assembly := s"${name.value}-${version.value}.sh.bat",

  scalacOptions ++= Seq("-Xmax-classfile-name", "127")
)

lazy val classpathJar = Seq(
  scriptClasspath := {
    val manifest = new java.util.jar.Manifest()
    manifest.getMainAttributes.putValue("Class-Path", scriptClasspath.value.mkString(" "))
    val classpathJar = (target in Universal).value / "lib" / "classpath.jar"
    IO.jar(Seq.empty, classpathJar, manifest)
    Seq("classpath.jar")
  },

  mappings in Universal += ((target in Universal).value / "lib" / "classpath.jar", "lib/classpath.jar")
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
    UniversalPlugin)
  .settings(settings: _*)
  .settings(classpathJar: _*)
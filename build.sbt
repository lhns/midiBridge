name := "MidiBridge"

lazy val settings = Seq(
  version := "0.0.0",

  scalaOrganization := "org.typelevel",
  scalaVersion := "2.11.8",

  resolvers := Seq("Artifactory" at "http://lolhens.no-ip.org/artifactory/libs-release/"),

  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
    "org.slf4j" % "slf4j-api" % "1.7.22",
    "ch.qos.logback" % "logback-classic" % "1.1.8",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "org.typelevel" %% "cats" % "0.8.1",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "com.github.mpilquist" %% "simulacrum" % "0.10.0",
    "io.monix" %% "monix" % "2.1.2",
    "io.monix" %% "monix-cats" % "2.1.2",
    "org.atnos" %% "eff" % "2.2.0",
    "com.typesafe.akka" %% "akka-actor" % "2.4.16",
    "com.typesafe.akka" %% "akka-remote" % "2.4.16",
    "com.typesafe.akka" %% "akka-stream" % "2.4.16",
    "io.spray" %% "spray-json" % "1.3.2",
    "com.github.fommil" %% "spray-json-shapeless" % "1.3.0",
    "org.scodec" % "scodec-bits_2.11" % "1.1.2",
    "org.jcodec" % "jcodec-javase" % "0.2.0",
    "org.jcodec" % "jcodec-samples" % "0.2.0",
    "io.swave" % "swave-core_2.11" % "0.6.0",
    "io.swave" % "swave-akka-compat_2.11" % "0.6.0",
    "io.swave" % "swave-scodec-compat_2.11" % "0.6.0",
    "com.github.julien-truffaut" %% "monocle-core" % "1.4.0-M2",
    "com.github.julien-truffaut" %% "monocle-macro" % "1.4.0-M2",
    "com.github.melrief" %% "pureconfig" % "0.4.0",
    "eu.timepit" %% "refined" % "0.6.1",
    "eu.timepit" %% "refined-pureconfig" % "0.6.1",
    "org.scalafx" %% "scalafx" % "8.0.102-R11",
    "com.miglayout" % "miglayout-javafx" % "5.1-SNAPSHOT"
  ),

  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  //addCompilerPlugin("com.milessabin" % "si2712fix-plugin_2.11.8" % "1.2.0"),

  mainClass in Compile := Some("org.lolhens.midibridge.Main"),

  fork in run := true,

  assemblyMergeStrategy in assembly := {
    case PathList("scala", _*) | PathList("library.properties") =>
      MergeStrategy.first
    case file => (assemblyMergeStrategy in assembly).value.apply(file)
  },

  dependencyUpdatesExclusions := moduleFilter(organization = "org.scala-lang"),

  scalacOptions ++= Seq("-Xmax-classfile-name", "254")
) ++ proguardSettings ++ Seq(
  ProguardKeys.proguardVersion in Proguard := "5.3.2",
  javaOptions in(Proguard, ProguardKeys.proguard) := Seq("-Xmx2G"),

  (ProguardKeys.options in Proguard) ++= (mainClass in Compile).value.map(mainClass => ProguardOptions.keepMain(mainClass)).toList,

  ProguardKeys.inputs in Proguard := Seq(baseDirectory.value / "target" / s"scala-${scalaVersion.value.dropRight(2)}" / s"${name.value}-assembly-${version.value}.jar"),

  ProguardKeys.inputFilter in Proguard := (_ => None),
  ProguardKeys.libraries in Proguard := Seq(),
  ProguardKeys.merge in Proguard := false,

  (ProguardKeys.options in Proguard) ++= {
    val libraryJars = Seq(
      "<java.home>/lib/rt.jar",
      "<java.home>/lib/ext/jfxrt.jar"
    )

    val settings = Seq(
      "dontnote", "dontwarn", "ignorewarnings",
      "dontobfuscate",
      "dontoptimize",
      "keepattributes Signature, *Annotation*",
      "keepclassmembers class * {** MODULE$;}"
    )

    val excluded = Seq(
      "* extends akka.dispatch.ExecutorServiceConfigurator",
      "* extends akka.dispatch.MessageDispatcherConfigurator",
      "* extends akka.remote.RemoteTransport",
      "* implements akka.actor.Actor",
      "* implements akka.actor.ActorRefProvider",
      "* implements akka.actor.ExtensionId",
      "* implements akka.actor.ExtensionIdProvider",
      "* implements akka.actor.SupervisorStrategyConfigurator",
      "* implements akka.dispatch.MailboxType",
      "* implements akka.routing.RouterConfig",
      "* implements akka.serialization.Serializer",
      "akka.*.*MessageQueueSemantics",
      "akka.actor.LightArrayRevolverScheduler",
      "akka.actor.LocalActorRefProvider",
      "akka.actor.SerializedActorRef",
      "akka.dispatch.MultipleConsumerSemantics",
      "akka.event.Logging$LogExt",
      "akka.event.Logging*",
      "akka.remote.DaemonMsgCreate",
      "akka.routing.ConsistentHashingPool",
      "akka.routing.RoutedActorCell$RouterActorCreator",
      "akka.event.DefaultLoggingFilter"
    )

    libraryJars.map(libraryJar => s"-libraryjars $libraryJar") ++
      settings.map(setting => s"-$setting") ++
      excluded.flatMap(clazz => List(s"-keep class $clazz {*;}", s"-keep interface $clazz {*;}"))
  },

  (ProguardKeys.proguard in Proguard) := (ProguardKeys.proguard in Proguard).dependsOn(assembly).value
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

lazy val root = project.in(file("."))
  .enablePlugins(
    JavaAppPackaging,
    UniversalPlugin)
  .settings(settings: _*)
  .settings(classpathJar: _*)
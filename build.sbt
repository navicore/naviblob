name := "NaviBlob"
organization := "tech.navicore"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8") 
scalacOptions ++= Seq(
  "-target:jvm-1.8"
)
fork := true
javaOptions in test ++= Seq(
  "-Xms512M", "-Xmx2048M",
  "-XX:MaxPermSize=2048M",
  "-XX:+CMSClassUnloadingEnabled"
)

parallelExecution in test := false

val akkaVersion = "2.6.14"
val scala212 = "2.12.14"

crossScalaVersions := Seq(scala212)
inThisBuild(List(
  organization := "tech.navicore",
  homepage := Some(url("https://github.com/navicore/naviblob")),
  licenses := List("MIT" -> url("https://github.com/navicore/naviblob/blob/master/LICENSE")),
  developers := List(
    Developer(
      "navicore",
      "Ed Sweeney",
      "ed@onextent.com",
      url("https://navicore.tech")
    )
  )
))

libraryDependencies ++=
  Seq(

    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe" % "config" % "1.4.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",


    "com.microsoft.azure" % "azure-storage" % "8.6.6",

    "com.sksamuel.avro4s" %% "avro4s-core" % "4.0.9",

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    "org.scalatest" %% "scalatest" % "3.2.9" % "test"
  )

dependencyOverrides ++= Seq(
)

mainClass in assembly := Some("onextent.akka.naviblob.cli.ToConsoleMain")

assemblyJarName in assembly := "NaviBlob.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}


name := """mcs"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"


resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"


libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"	

libraryDependencies += "com.typesafe.akka" %% "akka-agent" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.5"

libraryDependencies += "org.nights.npe" %% "npecommon" % "1.0.0"	

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)


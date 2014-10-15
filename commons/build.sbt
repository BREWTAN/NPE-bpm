
organization := "org.nights.npe"

name := """commons"""

version := "1.0.0"

scalaVersion := "2.11.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % "2.3.5"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.mvel" % "mvel2" % "2.2.2.Final"

libraryDependencies ++= Seq(
   "javax.transaction" % "jta" % "1.1",
   "com.github.mauricio" %% "mysql-async" % "0.2.14"
)



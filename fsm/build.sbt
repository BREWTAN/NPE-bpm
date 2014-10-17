import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

packageArchetype.java_application

name := """fsm"""

version := "1.0.0"

scalaVersion := "2.11.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-agent" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % "2.3.5"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.5"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

libraryDependencies += "org.mvel" % "mvel2" % "2.2.2.Final"

libraryDependencies += "org.nights.npe" %% "commons" % "1.0.0"	

libraryDependencies += "com.typesafe" % "config" % "1.2.0"

resolvers ++= Seq(
   "Apache Maven" at "http://mvnrepository.com/artifact",
   "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""

bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""

libraryDependencies ++= Seq(
   "net.sf.ehcache" % "ehcache" % "2.8.3" from "http://repo1.maven.org/maven2/net/sf/ehcache/ehcache/2.8.3/ehcache-2.8.3.pom",
   "net.sf.ehcache" % "ehcache-jgroupsreplication" % "1.7",
   "javax.transaction" % "jta" % "1.1",
   "com.github.mauricio" %% "mysql-async" % "0.2.14"
)



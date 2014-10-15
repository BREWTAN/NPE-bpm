import sbt._
import Keys._
import com.typesafe.sbt.SbtNativePackager._

import NativePackagerKeys._

object ApplicationBuild extends Build {

  lazy val appVersion = "1.0.0"

		 packageArchetype.java_application

}
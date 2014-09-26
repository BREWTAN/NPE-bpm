import sbt._
import Keys._

object obuild extends Build {

	lazy val commons = project.in( file("commons") )
//	lazy val mcs	= project.in(file("mcs"))
	lazy val fsm	= project.in(file("fsm")).dependsOn(commons)
	lazy val queue	= project.in(file("queue"))
	
	lazy val flows	= project.in(file("flows"))

	

}
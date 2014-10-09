// @SOURCE:/Users/brew/NPE/gits/mcs/conf/routes
// @HASH:788d8f4bcd0ebece1e04faef1636a29913ff15d7
// @DATE:Thu Oct 09 10:34:50 GMT 2014


import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset

import Router.queryString

object Routes extends Router.Routes {

import ReverseRouteContext.empty

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" }


// @LINE:6
private[this] lazy val controllers_Application_index0_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
private[this] lazy val controllers_Application_index0_invoker = createInvoker(
controllers.Application.index,
HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "index", Nil,"GET", """ Home page""", Routes.prefix + """"""))
        

// @LINE:9
private[this] lazy val controllers_Assets_at1_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+""",false))))
private[this] lazy val controllers_Assets_at1_invoker = createInvoker(
controllers.Assets.at(fakeValue[String], fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
        

// @LINE:14
private[this] lazy val controllers_FSMViewer_stats2_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("fsmstats"))))
private[this] lazy val controllers_FSMViewer_stats2_invoker = createInvoker(
controllers.FSMViewer.stats,
HandlerDef(this.getClass.getClassLoader, "", "controllers.FSMViewer", "stats", Nil,"GET", """""", Routes.prefix + """fsmstats"""))
        

// @LINE:16
private[this] lazy val controllers_Test1_getJson3_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("test1"))))
private[this] lazy val controllers_Test1_getJson3_invoker = createInvoker(
controllers.Test1.getJson,
HandlerDef(this.getClass.getClassLoader, "", "controllers.Test1", "getJson", Nil,"GET", """ Test API""", Routes.prefix + """test1"""))
        

// @LINE:21
private[this] lazy val controllers_ProcDefHttpFace_test4_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("uploadtest"))))
private[this] lazy val controllers_ProcDefHttpFace_test4_invoker = createInvoker(
controllers.ProcDefHttpFace.test,
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "test", Nil,"GET", """""", Routes.prefix + """uploadtest"""))
        

// @LINE:24
private[this] lazy val controllers_ProcDefHttpFace_upload5_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("upload"))))
private[this] lazy val controllers_ProcDefHttpFace_upload5_invoker = createInvoker(
controllers.ProcDefHttpFace.upload,
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "upload", Nil,"POST", """""", Routes.prefix + """upload"""))
        

// @LINE:28
private[this] lazy val controllers_ProcDefHttpFace_getByPage6_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procdef"))))
private[this] lazy val controllers_ProcDefHttpFace_getByPage6_invoker = createInvoker(
controllers.ProcDefHttpFace.getByPage(fakeValue[Int], fakeValue[Int]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "getByPage", Seq(classOf[Int], classOf[Int]),"GET", """# rest for procdef""", Routes.prefix + """procdef"""))
        

// @LINE:30
private[this] lazy val controllers_ProcDefHttpFace_validate7_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procdef/check"))))
private[this] lazy val controllers_ProcDefHttpFace_validate7_invoker = createInvoker(
controllers.ProcDefHttpFace.validate,
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "validate", Nil,"GET", """""", Routes.prefix + """procdef/check"""))
        

// @LINE:32
private[this] lazy val controllers_ProcDefHttpFace_delete8_route = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procdef/"),DynamicPart("defid", """[^/]+""",true))))
private[this] lazy val controllers_ProcDefHttpFace_delete8_invoker = createInvoker(
controllers.ProcDefHttpFace.delete(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "delete", Seq(classOf[String]),"DELETE", """""", Routes.prefix + """procdef/$defid<[^/]+>"""))
        

// @LINE:34
private[this] lazy val controllers_ProcDefHttpFace_update9_route = Route("PUT", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procdef/"),DynamicPart("defid", """[^/]+""",true))))
private[this] lazy val controllers_ProcDefHttpFace_update9_invoker = createInvoker(
controllers.ProcDefHttpFace.update(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "update", Seq(classOf[String]),"PUT", """""", Routes.prefix + """procdef/$defid<[^/]+>"""))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """fsmstats""","""controllers.FSMViewer.stats"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """test1""","""controllers.Test1.getJson"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """uploadtest""","""controllers.ProcDefHttpFace.test"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """upload""","""controllers.ProcDefHttpFace.upload"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef""","""controllers.ProcDefHttpFace.getByPage(skip:Int ?= 0, limit:Int ?= 10)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/check""","""controllers.ProcDefHttpFace.validate"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/$defid<[^/]+>""","""controllers.ProcDefHttpFace.delete(defid:String)"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/$defid<[^/]+>""","""controllers.ProcDefHttpFace.update(defid:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]]
}}
      

def routes:PartialFunction[RequestHeader,Handler] = {

// @LINE:6
case controllers_Application_index0_route(params) => {
   call { 
        controllers_Application_index0_invoker.call(controllers.Application.index)
   }
}
        

// @LINE:9
case controllers_Assets_at1_route(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        controllers_Assets_at1_invoker.call(controllers.Assets.at(path, file))
   }
}
        

// @LINE:14
case controllers_FSMViewer_stats2_route(params) => {
   call { 
        controllers_FSMViewer_stats2_invoker.call(controllers.FSMViewer.stats)
   }
}
        

// @LINE:16
case controllers_Test1_getJson3_route(params) => {
   call { 
        controllers_Test1_getJson3_invoker.call(controllers.Test1.getJson)
   }
}
        

// @LINE:21
case controllers_ProcDefHttpFace_test4_route(params) => {
   call { 
        controllers_ProcDefHttpFace_test4_invoker.call(controllers.ProcDefHttpFace.test)
   }
}
        

// @LINE:24
case controllers_ProcDefHttpFace_upload5_route(params) => {
   call { 
        controllers_ProcDefHttpFace_upload5_invoker.call(controllers.ProcDefHttpFace.upload)
   }
}
        

// @LINE:28
case controllers_ProcDefHttpFace_getByPage6_route(params) => {
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10))) { (skip, limit) =>
        controllers_ProcDefHttpFace_getByPage6_invoker.call(controllers.ProcDefHttpFace.getByPage(skip, limit))
   }
}
        

// @LINE:30
case controllers_ProcDefHttpFace_validate7_route(params) => {
   call { 
        controllers_ProcDefHttpFace_validate7_invoker.call(controllers.ProcDefHttpFace.validate)
   }
}
        

// @LINE:32
case controllers_ProcDefHttpFace_delete8_route(params) => {
   call(params.fromPath[String]("defid", None)) { (defid) =>
        controllers_ProcDefHttpFace_delete8_invoker.call(controllers.ProcDefHttpFace.delete(defid))
   }
}
        

// @LINE:34
case controllers_ProcDefHttpFace_update9_route(params) => {
   call(params.fromPath[String]("defid", None)) { (defid) =>
        controllers_ProcDefHttpFace_update9_invoker.call(controllers.ProcDefHttpFace.update(defid))
   }
}
        
}

}
     
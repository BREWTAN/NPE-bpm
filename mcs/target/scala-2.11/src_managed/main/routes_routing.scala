// @SOURCE:/home/brew/git/npe/mcs/conf/routes
// @HASH:9a856e5e252a17a731905846adac0cfc8c92d719
// @DATE:Wed Oct 15 00:17:54 CST 2014


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
controllers.ProcDefHttpFace.getByPage(fakeValue[Int], fakeValue[Int], fakeValue[Boolean]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Boolean]),"GET", """# rest for procdef""", Routes.prefix + """procdef"""))
        

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
        

// @LINE:36
private[this] lazy val controllers_ProcDefHttpFace_getMainProc10_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procdef/main"))))
private[this] lazy val controllers_ProcDefHttpFace_getMainProc10_invoker = createInvoker(
controllers.ProcDefHttpFace.getMainProc,
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "getMainProc", Nil,"GET", """""", Routes.prefix + """procdef/main"""))
        

// @LINE:41
private[this] lazy val controllers_ProcInstFace_getByPage11_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procinst"))))
private[this] lazy val controllers_ProcInstFace_getByPage11_invoker = createInvoker(
controllers.ProcInstFace.getByPage(fakeValue[Int], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[Boolean]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcInstFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]),"GET", """#############
 procinst""", Routes.prefix + """procinst"""))
        

// @LINE:43
private[this] lazy val controllers_ProcInstFace_delete12_route = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("procinst/"),DynamicPart("defid", """[^/]+""",true))))
private[this] lazy val controllers_ProcInstFace_delete12_invoker = createInvoker(
controllers.ProcInstFace.delete(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcInstFace", "delete", Seq(classOf[String]),"DELETE", """""", Routes.prefix + """procinst/$defid<[^/]+>"""))
        

// @LINE:48
private[this] lazy val controllers_TaskInstFace_getByPage13_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskinst"))))
private[this] lazy val controllers_TaskInstFace_getByPage13_invoker = createInvoker(
controllers.TaskInstFace.getByPage(fakeValue[Int], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[Boolean]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskInstFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]),"GET", """#############
 procinst""", Routes.prefix + """taskinst"""))
        

// @LINE:50
private[this] lazy val controllers_TaskInstFace_delete14_route = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskinst/"),DynamicPart("defid", """[^/]+""",true))))
private[this] lazy val controllers_TaskInstFace_delete14_invoker = createInvoker(
controllers.TaskInstFace.delete(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskInstFace", "delete", Seq(classOf[String]),"DELETE", """""", Routes.prefix + """taskinst/$defid<[^/]+>"""))
        

// @LINE:57
private[this] lazy val controllers_TaskCenterFace_getByPage15_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskcenter"))))
private[this] lazy val controllers_TaskCenterFace_getByPage15_invoker = createInvoker(
controllers.TaskCenterFace.getByPage(fakeValue[Int], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[Boolean]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]),"GET", """""", Routes.prefix + """taskcenter"""))
        

// @LINE:58
private[this] lazy val controllers_TaskCenterFace_delete16_route = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskcenter/"),DynamicPart("keyy", """[^/]+""",true))))
private[this] lazy val controllers_TaskCenterFace_delete16_invoker = createInvoker(
controllers.TaskCenterFace.delete(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "delete", Seq(classOf[String]),"DELETE", """""", Routes.prefix + """taskcenter/$keyy<[^/]+>"""))
        

// @LINE:59
private[this] lazy val controllers_TaskCenterFace_insert17_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskcenter"))))
private[this] lazy val controllers_TaskCenterFace_insert17_invoker = createInvoker(
controllers.TaskCenterFace.insert,
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "insert", Nil,"POST", """""", Routes.prefix + """taskcenter"""))
        

// @LINE:60
private[this] lazy val controllers_TaskCenterFace_update18_route = Route("PUT", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskcenter/"),DynamicPart("keyy", """[^/]+""",true))))
private[this] lazy val controllers_TaskCenterFace_update18_invoker = createInvoker(
controllers.TaskCenterFace.update(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "update", Seq(classOf[String]),"PUT", """""", Routes.prefix + """taskcenter/$keyy<[^/]+>"""))
        

// @LINE:65
private[this] lazy val controllers_TaskRoleFace_getByPage19_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskrole"))))
private[this] lazy val controllers_TaskRoleFace_getByPage19_invoker = createInvoker(
controllers.TaskRoleFace.getByPage(fakeValue[Int], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[Boolean]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]),"GET", """""", Routes.prefix + """taskrole"""))
        

// @LINE:66
private[this] lazy val controllers_TaskRoleFace_delete20_route = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskrole/"),DynamicPart("keyy", """[^/]+""",true))))
private[this] lazy val controllers_TaskRoleFace_delete20_invoker = createInvoker(
controllers.TaskRoleFace.delete(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "delete", Seq(classOf[String]),"DELETE", """""", Routes.prefix + """taskrole/$keyy<[^/]+>"""))
        

// @LINE:67
private[this] lazy val controllers_TaskRoleFace_insert21_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskrole"))))
private[this] lazy val controllers_TaskRoleFace_insert21_invoker = createInvoker(
controllers.TaskRoleFace.insert,
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "insert", Nil,"POST", """""", Routes.prefix + """taskrole"""))
        

// @LINE:68
private[this] lazy val controllers_TaskRoleFace_update22_route = Route("PUT", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskrole/"),DynamicPart("keyy", """[^/]+""",true))))
private[this] lazy val controllers_TaskRoleFace_update22_invoker = createInvoker(
controllers.TaskRoleFace.update(fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "update", Seq(classOf[String]),"PUT", """""", Routes.prefix + """taskrole/$keyy<[^/]+>"""))
        

// @LINE:70
private[this] lazy val controllers_TaskRoleFace_importFromFlows23_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("taskrole/import"))))
private[this] lazy val controllers_TaskRoleFace_importFromFlows23_invoker = createInvoker(
controllers.TaskRoleFace.importFromFlows,
HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "importFromFlows", Nil,"GET", """""", Routes.prefix + """taskrole/import"""))
        

// @LINE:75
private[this] lazy val controllers_QueueViewer_statsByName24_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("qstatbyname"))))
private[this] lazy val controllers_QueueViewer_statsByName24_invoker = createInvoker(
controllers.QueueViewer.statsByName,
HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueViewer", "statsByName", Nil,"GET", """################
 queue views""", Routes.prefix + """qstatbyname"""))
        

// @LINE:76
private[this] lazy val controllers_QueueViewer_statsByCenter25_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("qstatbycenter"))))
private[this] lazy val controllers_QueueViewer_statsByCenter25_invoker = createInvoker(
controllers.QueueViewer.statsByCenter,
HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueViewer", "statsByCenter", Nil,"GET", """""", Routes.prefix + """qstatbycenter"""))
        

// @LINE:81
private[this] lazy val controllers_QueueWorker_obtainByRole26_route = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("obtain"))))
private[this] lazy val controllers_QueueWorker_obtainByRole26_invoker = createInvoker(
controllers.QueueWorker.obtainByRole(fakeValue[String], fakeValue[String], fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueWorker", "obtainByRole", Seq(classOf[String], classOf[String], classOf[String]),"GET", """#################
 do work""", Routes.prefix + """obtain"""))
        

// @LINE:83
private[this] lazy val controllers_QueueWorker_submit27_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("submit"))))
private[this] lazy val controllers_QueueWorker_submit27_invoker = createInvoker(
controllers.QueueWorker.submit(),
HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueWorker", "submit", Nil,"POST", """""", Routes.prefix + """submit"""))
        

// @LINE:85
private[this] lazy val controllers_QueueWorker_newProc28_route = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("newproc"))))
private[this] lazy val controllers_QueueWorker_newProc28_invoker = createInvoker(
controllers.QueueWorker.newProc(fakeValue[String], fakeValue[String], fakeValue[String]),
HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueWorker", "newProc", Seq(classOf[String], classOf[String], classOf[String]),"POST", """""", Routes.prefix + """newproc"""))
        
def documentation = List(("""GET""", prefix,"""controllers.Application.index"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """fsmstats""","""controllers.FSMViewer.stats"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """test1""","""controllers.Test1.getJson"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """uploadtest""","""controllers.ProcDefHttpFace.test"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """upload""","""controllers.ProcDefHttpFace.upload"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef""","""controllers.ProcDefHttpFace.getByPage(skip:Int ?= 0, limit:Int ?= 10, page:Boolean ?= false)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/check""","""controllers.ProcDefHttpFace.validate"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/$defid<[^/]+>""","""controllers.ProcDefHttpFace.delete(defid:String)"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/$defid<[^/]+>""","""controllers.ProcDefHttpFace.update(defid:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procdef/main""","""controllers.ProcDefHttpFace.getMainProc"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procinst""","""controllers.ProcInstFace.getByPage(skip:Int ?= 0, limit:Int ?= 10, status:Int ?= 0, query:String ?= null, page:Boolean ?= false)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """procinst/$defid<[^/]+>""","""controllers.ProcInstFace.delete(defid:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskinst""","""controllers.TaskInstFace.getByPage(skip:Int ?= 0, limit:Int ?= 10, status:Int ?= 0, query:String ?= null, page:Boolean ?= false)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskinst/$defid<[^/]+>""","""controllers.TaskInstFace.delete(defid:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskcenter""","""controllers.TaskCenterFace.getByPage(skip:Int ?= 0, limit:Int ?= 10, status:Int ?= 0, query:String ?= null, page:Boolean ?= false)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskcenter/$keyy<[^/]+>""","""controllers.TaskCenterFace.delete(keyy:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskcenter""","""controllers.TaskCenterFace.insert"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskcenter/$keyy<[^/]+>""","""controllers.TaskCenterFace.update(keyy:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskrole""","""controllers.TaskRoleFace.getByPage(skip:Int ?= 0, limit:Int ?= 10, status:Int ?= 0, query:String ?= null, page:Boolean ?= false)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskrole/$keyy<[^/]+>""","""controllers.TaskRoleFace.delete(keyy:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskrole""","""controllers.TaskRoleFace.insert"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskrole/$keyy<[^/]+>""","""controllers.TaskRoleFace.update(keyy:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """taskrole/import""","""controllers.TaskRoleFace.importFromFlows"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """qstatbyname""","""controllers.QueueViewer.statsByName"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """qstatbycenter""","""controllers.QueueViewer.statsByCenter"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """obtain""","""controllers.QueueWorker.obtainByRole(obtainer:String ?= null, role:String ?= null, center:String ?= null)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """submit""","""controllers.QueueWorker.submit()"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """newproc""","""controllers.QueueWorker.newProc(submiter:String ?= null, center:String ?= null, procdef:String ?= null)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
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
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10)), params.fromQuery[Boolean]("page", Some(false))) { (skip, limit, page) =>
        controllers_ProcDefHttpFace_getByPage6_invoker.call(controllers.ProcDefHttpFace.getByPage(skip, limit, page))
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
        

// @LINE:36
case controllers_ProcDefHttpFace_getMainProc10_route(params) => {
   call { 
        controllers_ProcDefHttpFace_getMainProc10_invoker.call(controllers.ProcDefHttpFace.getMainProc)
   }
}
        

// @LINE:41
case controllers_ProcInstFace_getByPage11_route(params) => {
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10)), params.fromQuery[Int]("status", Some(0)), params.fromQuery[String]("query", Some(null)), params.fromQuery[Boolean]("page", Some(false))) { (skip, limit, status, query, page) =>
        controllers_ProcInstFace_getByPage11_invoker.call(controllers.ProcInstFace.getByPage(skip, limit, status, query, page))
   }
}
        

// @LINE:43
case controllers_ProcInstFace_delete12_route(params) => {
   call(params.fromPath[String]("defid", None)) { (defid) =>
        controllers_ProcInstFace_delete12_invoker.call(controllers.ProcInstFace.delete(defid))
   }
}
        

// @LINE:48
case controllers_TaskInstFace_getByPage13_route(params) => {
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10)), params.fromQuery[Int]("status", Some(0)), params.fromQuery[String]("query", Some(null)), params.fromQuery[Boolean]("page", Some(false))) { (skip, limit, status, query, page) =>
        controllers_TaskInstFace_getByPage13_invoker.call(controllers.TaskInstFace.getByPage(skip, limit, status, query, page))
   }
}
        

// @LINE:50
case controllers_TaskInstFace_delete14_route(params) => {
   call(params.fromPath[String]("defid", None)) { (defid) =>
        controllers_TaskInstFace_delete14_invoker.call(controllers.TaskInstFace.delete(defid))
   }
}
        

// @LINE:57
case controllers_TaskCenterFace_getByPage15_route(params) => {
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10)), params.fromQuery[Int]("status", Some(0)), params.fromQuery[String]("query", Some(null)), params.fromQuery[Boolean]("page", Some(false))) { (skip, limit, status, query, page) =>
        controllers_TaskCenterFace_getByPage15_invoker.call(controllers.TaskCenterFace.getByPage(skip, limit, status, query, page))
   }
}
        

// @LINE:58
case controllers_TaskCenterFace_delete16_route(params) => {
   call(params.fromPath[String]("keyy", None)) { (keyy) =>
        controllers_TaskCenterFace_delete16_invoker.call(controllers.TaskCenterFace.delete(keyy))
   }
}
        

// @LINE:59
case controllers_TaskCenterFace_insert17_route(params) => {
   call { 
        controllers_TaskCenterFace_insert17_invoker.call(controllers.TaskCenterFace.insert)
   }
}
        

// @LINE:60
case controllers_TaskCenterFace_update18_route(params) => {
   call(params.fromPath[String]("keyy", None)) { (keyy) =>
        controllers_TaskCenterFace_update18_invoker.call(controllers.TaskCenterFace.update(keyy))
   }
}
        

// @LINE:65
case controllers_TaskRoleFace_getByPage19_route(params) => {
   call(params.fromQuery[Int]("skip", Some(0)), params.fromQuery[Int]("limit", Some(10)), params.fromQuery[Int]("status", Some(0)), params.fromQuery[String]("query", Some(null)), params.fromQuery[Boolean]("page", Some(false))) { (skip, limit, status, query, page) =>
        controllers_TaskRoleFace_getByPage19_invoker.call(controllers.TaskRoleFace.getByPage(skip, limit, status, query, page))
   }
}
        

// @LINE:66
case controllers_TaskRoleFace_delete20_route(params) => {
   call(params.fromPath[String]("keyy", None)) { (keyy) =>
        controllers_TaskRoleFace_delete20_invoker.call(controllers.TaskRoleFace.delete(keyy))
   }
}
        

// @LINE:67
case controllers_TaskRoleFace_insert21_route(params) => {
   call { 
        controllers_TaskRoleFace_insert21_invoker.call(controllers.TaskRoleFace.insert)
   }
}
        

// @LINE:68
case controllers_TaskRoleFace_update22_route(params) => {
   call(params.fromPath[String]("keyy", None)) { (keyy) =>
        controllers_TaskRoleFace_update22_invoker.call(controllers.TaskRoleFace.update(keyy))
   }
}
        

// @LINE:70
case controllers_TaskRoleFace_importFromFlows23_route(params) => {
   call { 
        controllers_TaskRoleFace_importFromFlows23_invoker.call(controllers.TaskRoleFace.importFromFlows)
   }
}
        

// @LINE:75
case controllers_QueueViewer_statsByName24_route(params) => {
   call { 
        controllers_QueueViewer_statsByName24_invoker.call(controllers.QueueViewer.statsByName)
   }
}
        

// @LINE:76
case controllers_QueueViewer_statsByCenter25_route(params) => {
   call { 
        controllers_QueueViewer_statsByCenter25_invoker.call(controllers.QueueViewer.statsByCenter)
   }
}
        

// @LINE:81
case controllers_QueueWorker_obtainByRole26_route(params) => {
   call(params.fromQuery[String]("obtainer", Some(null)), params.fromQuery[String]("role", Some(null)), params.fromQuery[String]("center", Some(null))) { (obtainer, role, center) =>
        controllers_QueueWorker_obtainByRole26_invoker.call(controllers.QueueWorker.obtainByRole(obtainer, role, center))
   }
}
        

// @LINE:83
case controllers_QueueWorker_submit27_route(params) => {
   call { 
        controllers_QueueWorker_submit27_invoker.call(controllers.QueueWorker.submit())
   }
}
        

// @LINE:85
case controllers_QueueWorker_newProc28_route(params) => {
   call(params.fromQuery[String]("submiter", Some(null)), params.fromQuery[String]("center", Some(null)), params.fromQuery[String]("procdef", Some(null))) { (submiter, center, procdef) =>
        controllers_QueueWorker_newProc28_invoker.call(controllers.QueueWorker.newProc(submiter, center, procdef))
   }
}
        
}

}
     
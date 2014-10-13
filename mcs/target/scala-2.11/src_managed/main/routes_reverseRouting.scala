// @SOURCE:/Users/brew/NPE/gits/mcs/conf/routes
// @HASH:789c4b4f8f736a1947d0c986ced5f41833e5b0eb
// @DATE:Mon Oct 13 09:53:30 GMT 2014

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset

import Router.queryString


// @LINE:66
// @LINE:65
// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
// @LINE:41
// @LINE:39
// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
// @LINE:16
// @LINE:14
// @LINE:9
// @LINE:6
package controllers {

// @LINE:9
class ReverseAssets {


// @LINE:9
def at(file:String): Call = {
   implicit val _rrc = new ReverseRouteContext(Map(("path", "/public")))
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                        

}
                          

// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseTaskRoleFace {


// @LINE:60
def importFromFlows(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "taskrole/import")
}
                        

// @LINE:57
def insert(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "taskrole")
}
                        

// @LINE:56
def delete(keyy:String): Call = {
   import ReverseRouteContext.empty
   Call("DELETE", _prefix + { _defaultPrefix } + "taskrole/" + implicitly[PathBindable[String]].unbind("keyy", dynamicString(keyy)))
}
                        

// @LINE:58
def update(keyy:String): Call = {
   import ReverseRouteContext.empty
   Call("PUT", _prefix + { _defaultPrefix } + "taskrole/" + implicitly[PathBindable[String]].unbind("keyy", dynamicString(keyy)))
}
                        

// @LINE:55
def getByPage(skip:Int = 0, limit:Int = 10, status:Int = 0, query:String = null, page:Boolean = false): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "taskrole" + queryString(List(if(skip == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("skip", skip)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)), if(status == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("status", status)), if(query == null) None else Some(implicitly[QueryStringBindable[String]].unbind("query", query)), if(page == false) None else Some(implicitly[QueryStringBindable[Boolean]].unbind("page", page)))))
}
                        

}
                          

// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
class ReverseTaskCenterFace {


// @LINE:48
def delete(keyy:String): Call = {
   import ReverseRouteContext.empty
   Call("DELETE", _prefix + { _defaultPrefix } + "taskcenter/" + implicitly[PathBindable[String]].unbind("keyy", dynamicString(keyy)))
}
                        

// @LINE:49
def insert(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "taskcenter")
}
                        

// @LINE:47
def getByPage(skip:Int = 0, limit:Int = 10, status:Int = 0, query:String = null, page:Boolean = false): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "taskcenter" + queryString(List(if(skip == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("skip", skip)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)), if(status == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("status", status)), if(query == null) None else Some(implicitly[QueryStringBindable[String]].unbind("query", query)), if(page == false) None else Some(implicitly[QueryStringBindable[Boolean]].unbind("page", page)))))
}
                        

// @LINE:50
def update(keyy:String): Call = {
   import ReverseRouteContext.empty
   Call("PUT", _prefix + { _defaultPrefix } + "taskcenter/" + implicitly[PathBindable[String]].unbind("keyy", dynamicString(keyy)))
}
                        

}
                          

// @LINE:41
// @LINE:39
class ReverseProcInstFace {


// @LINE:41
def delete(defid:String): Call = {
   import ReverseRouteContext.empty
   Call("DELETE", _prefix + { _defaultPrefix } + "procinst/" + implicitly[PathBindable[String]].unbind("defid", dynamicString(defid)))
}
                        

// @LINE:39
def getByPage(skip:Int = 0, limit:Int = 10, status:Int = 0, query:String = null, page:Boolean = false): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "procinst" + queryString(List(if(skip == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("skip", skip)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)), if(status == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("status", status)), if(query == null) None else Some(implicitly[QueryStringBindable[String]].unbind("query", query)), if(page == false) None else Some(implicitly[QueryStringBindable[Boolean]].unbind("page", page)))))
}
                        

}
                          

// @LINE:14
class ReverseFSMViewer {


// @LINE:14
def stats(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "fsmstats")
}
                        

}
                          

// @LINE:16
class ReverseTest1 {


// @LINE:16
def getJson(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "test1")
}
                        

}
                          

// @LINE:66
// @LINE:65
class ReverseQueueViewer {


// @LINE:66
def statsByCenter(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "qstatbycenter")
}
                        

// @LINE:65
def statsByName(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "qstatbyname")
}
                        

}
                          

// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
class ReverseProcDefHttpFace {


// @LINE:24
def upload(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "upload")
}
                        

// @LINE:28
def getByPage(skip:Int = 0, limit:Int = 10, page:Boolean = false): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "procdef" + queryString(List(if(skip == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("skip", skip)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)), if(page == false) None else Some(implicitly[QueryStringBindable[Boolean]].unbind("page", page)))))
}
                        

// @LINE:21
def test(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "uploadtest")
}
                        

// @LINE:32
def delete(defid:String): Call = {
   import ReverseRouteContext.empty
   Call("DELETE", _prefix + { _defaultPrefix } + "procdef/" + implicitly[PathBindable[String]].unbind("defid", dynamicString(defid)))
}
                        

// @LINE:34
def update(defid:String): Call = {
   import ReverseRouteContext.empty
   Call("PUT", _prefix + { _defaultPrefix } + "procdef/" + implicitly[PathBindable[String]].unbind("defid", dynamicString(defid)))
}
                        

// @LINE:30
def validate(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "procdef/check")
}
                        

}
                          

// @LINE:6
class ReverseApplication {


// @LINE:6
def index(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix)
}
                        

}
                          
}
                  


// @LINE:66
// @LINE:65
// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
// @LINE:41
// @LINE:39
// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
// @LINE:16
// @LINE:14
// @LINE:9
// @LINE:6
package controllers.javascript {
import ReverseRouteContext.empty

// @LINE:9
class ReverseAssets {


// @LINE:9
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        

}
              

// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseTaskRoleFace {


// @LINE:60
def importFromFlows : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskRoleFace.importFromFlows",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "taskrole/import"})
      }
   """
)
                        

// @LINE:57
def insert : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskRoleFace.insert",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "taskrole"})
      }
   """
)
                        

// @LINE:56
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskRoleFace.delete",
   """
      function(keyy) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "taskrole/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("keyy", encodeURIComponent(keyy))})
      }
   """
)
                        

// @LINE:58
def update : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskRoleFace.update",
   """
      function(keyy) {
      return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "taskrole/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("keyy", encodeURIComponent(keyy))})
      }
   """
)
                        

// @LINE:55
def getByPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskRoleFace.getByPage",
   """
      function(skip,limit,status,query,page) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "taskrole" + _qS([(skip == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("skip", skip)), (limit == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("limit", limit)), (status == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("status", status)), (query == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("query", query)), (page == null ? null : (""" + implicitly[QueryStringBindable[Boolean]].javascriptUnbind + """)("page", page))])})
      }
   """
)
                        

}
              

// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
class ReverseTaskCenterFace {


// @LINE:48
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskCenterFace.delete",
   """
      function(keyy) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "taskcenter/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("keyy", encodeURIComponent(keyy))})
      }
   """
)
                        

// @LINE:49
def insert : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskCenterFace.insert",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "taskcenter"})
      }
   """
)
                        

// @LINE:47
def getByPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskCenterFace.getByPage",
   """
      function(skip,limit,status,query,page) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "taskcenter" + _qS([(skip == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("skip", skip)), (limit == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("limit", limit)), (status == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("status", status)), (query == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("query", query)), (page == null ? null : (""" + implicitly[QueryStringBindable[Boolean]].javascriptUnbind + """)("page", page))])})
      }
   """
)
                        

// @LINE:50
def update : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.TaskCenterFace.update",
   """
      function(keyy) {
      return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "taskcenter/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("keyy", encodeURIComponent(keyy))})
      }
   """
)
                        

}
              

// @LINE:41
// @LINE:39
class ReverseProcInstFace {


// @LINE:41
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcInstFace.delete",
   """
      function(defid) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "procinst/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("defid", encodeURIComponent(defid))})
      }
   """
)
                        

// @LINE:39
def getByPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcInstFace.getByPage",
   """
      function(skip,limit,status,query,page) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "procinst" + _qS([(skip == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("skip", skip)), (limit == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("limit", limit)), (status == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("status", status)), (query == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("query", query)), (page == null ? null : (""" + implicitly[QueryStringBindable[Boolean]].javascriptUnbind + """)("page", page))])})
      }
   """
)
                        

}
              

// @LINE:14
class ReverseFSMViewer {


// @LINE:14
def stats : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.FSMViewer.stats",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "fsmstats"})
      }
   """
)
                        

}
              

// @LINE:16
class ReverseTest1 {


// @LINE:16
def getJson : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Test1.getJson",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "test1"})
      }
   """
)
                        

}
              

// @LINE:66
// @LINE:65
class ReverseQueueViewer {


// @LINE:66
def statsByCenter : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.QueueViewer.statsByCenter",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "qstatbycenter"})
      }
   """
)
                        

// @LINE:65
def statsByName : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.QueueViewer.statsByName",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "qstatbyname"})
      }
   """
)
                        

}
              

// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
class ReverseProcDefHttpFace {


// @LINE:24
def upload : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.upload",
   """
      function() {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "upload"})
      }
   """
)
                        

// @LINE:28
def getByPage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.getByPage",
   """
      function(skip,limit,page) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "procdef" + _qS([(skip == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("skip", skip)), (limit == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("limit", limit)), (page == null ? null : (""" + implicitly[QueryStringBindable[Boolean]].javascriptUnbind + """)("page", page))])})
      }
   """
)
                        

// @LINE:21
def test : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.test",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "uploadtest"})
      }
   """
)
                        

// @LINE:32
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.delete",
   """
      function(defid) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "procdef/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("defid", encodeURIComponent(defid))})
      }
   """
)
                        

// @LINE:34
def update : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.update",
   """
      function(defid) {
      return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "procdef/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("defid", encodeURIComponent(defid))})
      }
   """
)
                        

// @LINE:30
def validate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.validate",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "procdef/check"})
      }
   """
)
                        

}
              

// @LINE:6
class ReverseApplication {


// @LINE:6
def index : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        

}
              
}
        


// @LINE:66
// @LINE:65
// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
// @LINE:41
// @LINE:39
// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
// @LINE:16
// @LINE:14
// @LINE:9
// @LINE:6
package controllers.ref {


// @LINE:9
class ReverseAssets {


// @LINE:9
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this.getClass.getClassLoader, "", "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      

}
                          

// @LINE:60
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseTaskRoleFace {


// @LINE:60
def importFromFlows(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskRoleFace.importFromFlows(), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "importFromFlows", Seq(), "GET", """""", _prefix + """taskrole/import""")
)
                      

// @LINE:57
def insert(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskRoleFace.insert(), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "insert", Seq(), "POST", """""", _prefix + """taskrole""")
)
                      

// @LINE:56
def delete(keyy:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskRoleFace.delete(keyy), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "delete", Seq(classOf[String]), "DELETE", """""", _prefix + """taskrole/$keyy<[^/]+>""")
)
                      

// @LINE:58
def update(keyy:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskRoleFace.update(keyy), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "update", Seq(classOf[String]), "PUT", """""", _prefix + """taskrole/$keyy<[^/]+>""")
)
                      

// @LINE:55
def getByPage(skip:Int, limit:Int, status:Int, query:String, page:Boolean): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskRoleFace.getByPage(skip, limit, status, query, page), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskRoleFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]), "GET", """""", _prefix + """taskrole""")
)
                      

}
                          

// @LINE:50
// @LINE:49
// @LINE:48
// @LINE:47
class ReverseTaskCenterFace {


// @LINE:48
def delete(keyy:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskCenterFace.delete(keyy), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "delete", Seq(classOf[String]), "DELETE", """""", _prefix + """taskcenter/$keyy<[^/]+>""")
)
                      

// @LINE:49
def insert(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskCenterFace.insert(), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "insert", Seq(), "POST", """""", _prefix + """taskcenter""")
)
                      

// @LINE:47
def getByPage(skip:Int, limit:Int, status:Int, query:String, page:Boolean): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskCenterFace.getByPage(skip, limit, status, query, page), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]), "GET", """""", _prefix + """taskcenter""")
)
                      

// @LINE:50
def update(keyy:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.TaskCenterFace.update(keyy), HandlerDef(this.getClass.getClassLoader, "", "controllers.TaskCenterFace", "update", Seq(classOf[String]), "PUT", """""", _prefix + """taskcenter/$keyy<[^/]+>""")
)
                      

}
                          

// @LINE:41
// @LINE:39
class ReverseProcInstFace {


// @LINE:41
def delete(defid:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcInstFace.delete(defid), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcInstFace", "delete", Seq(classOf[String]), "DELETE", """""", _prefix + """procinst/$defid<[^/]+>""")
)
                      

// @LINE:39
def getByPage(skip:Int, limit:Int, status:Int, query:String, page:Boolean): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcInstFace.getByPage(skip, limit, status, query, page), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcInstFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Int], classOf[String], classOf[Boolean]), "GET", """#############
 procinst""", _prefix + """procinst""")
)
                      

}
                          

// @LINE:14
class ReverseFSMViewer {


// @LINE:14
def stats(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.FSMViewer.stats(), HandlerDef(this.getClass.getClassLoader, "", "controllers.FSMViewer", "stats", Seq(), "GET", """""", _prefix + """fsmstats""")
)
                      

}
                          

// @LINE:16
class ReverseTest1 {


// @LINE:16
def getJson(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Test1.getJson(), HandlerDef(this.getClass.getClassLoader, "", "controllers.Test1", "getJson", Seq(), "GET", """ Test API""", _prefix + """test1""")
)
                      

}
                          

// @LINE:66
// @LINE:65
class ReverseQueueViewer {


// @LINE:66
def statsByCenter(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.QueueViewer.statsByCenter(), HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueViewer", "statsByCenter", Seq(), "GET", """""", _prefix + """qstatbycenter""")
)
                      

// @LINE:65
def statsByName(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.QueueViewer.statsByName(), HandlerDef(this.getClass.getClassLoader, "", "controllers.QueueViewer", "statsByName", Seq(), "GET", """################
 queue views""", _prefix + """qstatbyname""")
)
                      

}
                          

// @LINE:34
// @LINE:32
// @LINE:30
// @LINE:28
// @LINE:24
// @LINE:21
class ReverseProcDefHttpFace {


// @LINE:24
def upload(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.upload(), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "upload", Seq(), "POST", """""", _prefix + """upload""")
)
                      

// @LINE:28
def getByPage(skip:Int, limit:Int, page:Boolean): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.getByPage(skip, limit, page), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "getByPage", Seq(classOf[Int], classOf[Int], classOf[Boolean]), "GET", """# rest for procdef""", _prefix + """procdef""")
)
                      

// @LINE:21
def test(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.test(), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "test", Seq(), "GET", """""", _prefix + """uploadtest""")
)
                      

// @LINE:32
def delete(defid:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.delete(defid), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "delete", Seq(classOf[String]), "DELETE", """""", _prefix + """procdef/$defid<[^/]+>""")
)
                      

// @LINE:34
def update(defid:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.update(defid), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "update", Seq(classOf[String]), "PUT", """""", _prefix + """procdef/$defid<[^/]+>""")
)
                      

// @LINE:30
def validate(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.validate(), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "validate", Seq(), "GET", """""", _prefix + """procdef/check""")
)
                      

}
                          

// @LINE:6
class ReverseApplication {


// @LINE:6
def index(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.index(), HandlerDef(this.getClass.getClassLoader, "", "controllers.Application", "index", Seq(), "GET", """ Home page""", _prefix + """""")
)
                      

}
                          
}
        
    
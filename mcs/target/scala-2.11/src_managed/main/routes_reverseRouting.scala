// @SOURCE:/Users/brew/NPE/gits/mcs/conf/routes
// @HASH:788d8f4bcd0ebece1e04faef1636a29913ff15d7
// @DATE:Thu Oct 09 10:34:50 GMT 2014

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset

import Router.queryString


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
def getByPage(skip:Int = 0, limit:Int = 10): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "procdef" + queryString(List(if(skip == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("skip", skip)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)))))
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
      function(skip,limit) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "procdef" + _qS([(skip == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("skip", skip)), (limit == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("limit", limit))])})
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
def getByPage(skip:Int, limit:Int): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.getByPage(skip, limit), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "getByPage", Seq(classOf[Int], classOf[Int]), "GET", """# rest for procdef""", _prefix + """procdef""")
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
        
    
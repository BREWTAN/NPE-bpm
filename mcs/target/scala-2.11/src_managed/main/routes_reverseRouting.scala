// @SOURCE:/Users/brew/NPE/workspace/npe/mcs/conf/routes
// @HASH:43d87ca3c695665537e3c4513a186116a4c5eed6
// @DATE:Fri Sep 12 10:31:30 GMT 2014

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.Router.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._
import _root_.controllers.Assets.Asset

import Router.queryString


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
                          

// @LINE:24
// @LINE:21
class ReverseProcDefHttpFace {


// @LINE:24
def upload(): Call = {
   import ReverseRouteContext.empty
   Call("POST", _prefix + { _defaultPrefix } + "upload")
}
                        

// @LINE:21
def test(): Call = {
   import ReverseRouteContext.empty
   Call("GET", _prefix + { _defaultPrefix } + "uploadtest")
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
                        

// @LINE:21
def test : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.ProcDefHttpFace.test",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "uploadtest"})
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
                          

// @LINE:24
// @LINE:21
class ReverseProcDefHttpFace {


// @LINE:24
def upload(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.upload(), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "upload", Seq(), "POST", """""", _prefix + """upload""")
)
                      

// @LINE:21
def test(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.ProcDefHttpFace.test(), HandlerDef(this.getClass.getClassLoader, "", "controllers.ProcDefHttpFace", "test", Seq(), "GET", """""", _prefix + """uploadtest""")
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
        
    
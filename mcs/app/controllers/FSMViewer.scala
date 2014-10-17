package controllers

import org.nights.npe.mcs.akka.Global
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.json.Json
object FSMViewer extends Controller {

  def stats = Action {
    Ok("" + Global.statsList.map({ set =>
      set.asInstanceOf[scala.collection.mutable.ArrayBuffer[Any]].map({ s1 =>
        val s = s1 match {
          case fps: String => fps
        }
        s
      }).mkString("[", ",", "]")
    }).mkString("[", ",", "]"))
  }
  def nodestats = Action {
    if (Global.statsList.size > 0) {
      val last = Global.statsList.last.asInstanceOf[scala.collection.mutable.ArrayBuffer[Any]]
      val jsonRet = Json.obj("total_rows" -> last.size,
        "count" -> last.size,
        "fromRow" -> 0,
        "limit" -> 100000,
        "rows" -> last.map({ set =>
          Json.parse(set.toString)
        }))

      Ok(jsonRet)
    } else {
      Ok("{}")
    }
  }
  def reloadprocdef = Action {
    Global.members.foreach(f => {
      val name = f._1.toString();
      if (f._2.contains("compute")) {
        val cmd = Global.remote.actorSelection(f._1.toString() + "/user/fsm/definitionstore")
        cmd! "reloadfromdb"
      }
    })
    Ok("{}")
  }
  
  def stop(names:String) = Action {
    Global.members.filter(n=> names.contains(","+n._1) ).foreach(f => {
      val name = f._1.toString();
      if (f._2.contains("compute")) {
        val cmd = Global.remote.actorSelection(f._1.toString() + "/user/fsm/definitionstore")
        cmd! "stop"
      }
    })
    Ok("{}")
  }

}
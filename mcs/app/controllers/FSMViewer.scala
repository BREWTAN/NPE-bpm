package controllers

import org.nights.npe.mcs.akka.Global

import play.api.mvc.Action
import play.api.mvc.Controller
object FSMViewer extends Controller {

  def stats = Action {
    Ok("" + Global.statsList.map({ set =>
      set.asInstanceOf[scala.collection.mutable.HashSet[Any]].map({ s1 =>
        val s = s1 match {
          case fps: String => fps
        }
        s
      }).mkString("[", ",", "]")
    }).mkString("[", ",", "]"))
  }
}
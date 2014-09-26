package controllers

import play.api.mvc.Action
import play.api.mvc.Controller

object Test1 extends Controller {

  def getJson = Action {
    Ok("jsonhell222llljlso");
  }

}
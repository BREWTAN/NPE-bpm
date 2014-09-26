package org.nights.npe.po

import scala.collection.mutable.HashMap
import scala.collection.mutable.MutableList

object Definition {

  case class _ele(var id: String, var name: String)

  trait _Node {
    def e: _ele
    var froms: MutableList[SequenceFlow] = MutableList.empty
    var tos: MutableList[SequenceFlow] = MutableList.empty
  }

  case class StartEvent(implicit var e: _ele) extends _Node

  case class Process(
    var e: _ele,
    var isvalid: Boolean = true,
    var Package: String = null,
    var Version: String = null,
    var vars: HashMap[String, Any] = HashMap.empty,
    var nodes: HashMap[String, _Node] = HashMap.empty,
    var starEvent: StartEvent = null,
    var parseError: MutableList[String] = MutableList.empty,
    var flows: MutableList[SequenceFlow] = MutableList.empty) extends _Node

  case class SubProcess(var innerProc: Process)(
    implicit var e: _ele) extends _Node

  case class CallActivity(
    var calledElement: String)(
      implicit var e: _ele,
      var entryActions: MutableList[String] = MutableList.empty,
      var exitActions: MutableList[String] = MutableList.empty,
      var paramMapping: HashMap[String, String] = HashMap.empty,
      var resultMapping: HashMap[String, String] = HashMap.empty) extends _Node

  case class EndEvent(
    implicit var e: _ele,
    var terminated: Boolean = false) extends _Node

  case class Gateway(var gatewaytype: String, var direction: String)(
    implicit var e: _ele) extends _Node

  case class UserTask(
    implicit var e: _ele,
    var actorId: String = null,
    var comment: String = null,
    var content: String = null,
    var groupId: String = null,
    var priority: Integer = 0,
    var entryActions: MutableList[String] = MutableList.empty,
    var exitActions: MutableList[String] = MutableList.empty,
    var paramMapping: HashMap[String, String] = HashMap.empty,
    var resultMapping: HashMap[String, String] = HashMap.empty,
    

    var taskName: String = null) extends _Node

  case class ScriptTask(
    var action: String)(
      implicit var e: _ele) extends _Node

  case class RuleTask(
    implicit var e: _ele) extends _Node

  case class SequenceFlow(
    var id: String,
    var sourceId: String,
    var targetId: String,
    var sourceRef: _Node = null,
    var targetRef: _Node = null)

}
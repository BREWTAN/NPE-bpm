package org.nights.npe.po

import scala.collection.mutable.HashMap
import scala.collection.mutable.MutableList

object Definition {

  final val NodeStart: String = "__start"
  case class _ele(val id: String, val name: String)

  trait _Node {
    def e: _ele
    val froms: MutableList[SequenceFlow] = MutableList.empty
    val tos: MutableList[SequenceFlow] = MutableList.empty
    val next: MutableList[_Node] = MutableList.empty
    def id: String = e.id
    def taskName:String = e.name 

  }
  val NoneEle: _ele = _ele("", "")

  case class NoneNode(e: _ele = NoneEle) extends _Node

  case class StartEvent(implicit val e: _ele) extends _Node

  case class Process(
    val e: _ele,
    var isvalid: Boolean = true,
    val Package: String = null,
    val Version: String = null,
    val vars: HashMap[String, Any] = HashMap.empty,
    val nodes: HashMap[String, _Node] = HashMap.empty,
    val parseError: MutableList[String] = MutableList.empty,
    var endNodesCount: Int=0, 
    val flows: MutableList[SequenceFlow] = MutableList.empty,
    var xmlBody :String = null) extends _Node {

    def getNode(key: String): _Node = nodes.getOrElse(key, NoneNode.asInstanceOf[_Node])

    def nextNodesFrom(cur: String): MutableList[_Node] = getNode(cur) next

    def startNode(): _Node = getNode(NodeStart)

  }

  val NoneProcess: Process = new Process(NoneEle)

  case class SubProcess(val innerProc: Process)(
    implicit val e: _ele) extends _Node

  case class CallActivity(
    val calledElement: String)(
      implicit val e: _ele,
      val entryActions: MutableList[String] = MutableList.empty,
      val exitActions: MutableList[String] = MutableList.empty,
      val paramMapping: HashMap[String, String] = HashMap.empty,
      val resultMapping: HashMap[String, String] = HashMap.empty) extends _Node

  case class EndEvent(
    implicit val e: _ele,
    val terminated: Boolean = false) extends _Node

  case class Gateway(val gatewaytype: String, val direction: String)(
    implicit val e: _ele) extends _Node

  case class ANDDiverging(direction:String="Diverging")(implicit val e: _ele) extends _Node

  case class XORDiverging(direction:String="Diverging")(implicit val e: _ele) extends _Node

  case class ORDiverging(direction:String="Diverging")(implicit val e: _ele) extends _Node

  case class ANDConverging(direction:String="Converging")(implicit val e: _ele) extends _Node

  case class XORConverging(direction:String="Converging")(implicit val e: _ele) extends _Node

  case class UserTask(
    val actorId: String = null,
//    val comment: String = null,
//    val content: String = null,
//    val groupId: String = null,
    val priority: Integer = 0,
    override val taskName: String = null,
    val entryActions: MutableList[String] = MutableList.empty,
    val exitActions: MutableList[String] = MutableList.empty,
    val paramMapping: HashMap[String, String] = HashMap.empty,
    val resultMapping: HashMap[String, String] = HashMap.empty)(implicit val e: _ele) extends _Node

  case class ScriptTask(
    val action: String)(
      implicit val e: _ele) extends _Node

  case class RuleTask(
    implicit val e: _ele) extends _Node

  case class SequenceFlow(
    val id: String,
    val sourceId: String,
    val targetId: String,
    val hasContraint:Option[Any] =None,
    val priority: String = null,
    val xsitype: String = null,
    val expression: String = null) 

}
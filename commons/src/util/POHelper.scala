package org.nights.npe.util

import scala.xml.Elem
import scala.xml.Node
import org.nights.npe.po.Definition._
import org.slf4j.LoggerFactory
import akka.event.slf4j.Logger

object POHelper {
  val log = LoggerFactory.getLogger("POHelper")

  def attrv(key: String)(implicit xmlnode: Node): String = {
    (xmlnode \ key).text
  } 

  def ele(implicit xmlnode: Node): _ele = {
    new _ele(attrv("@id"), attrv("@name"))
  }

  def parse(implicit xmlnode: Node, process: Process): Any = {
    implicit val _node = ele;
    xmlnode.label match {
      case "startEvent" =>
        process.starEvent = new StartEvent; process.starEvent
      case "callActivity" => new CallActivity(attrv("@calledElement"))
      case "subProcess" => new SubProcess(ParseProcess(xmlnode));
      case "endEvent" => new EndEvent
      case "userTask" => new UserTask
      case "scriptTask" => new ScriptTask(attrv("script"))
      case "businessRuleTask" => new RuleTask
      case "inclusiveGateway" => new Gateway("inclusive", attrv("@gatewayDirection"))
      case "exclusiveGateway" => new Gateway("exclusive", attrv("@gatewayDirection"))
      case "complexGateway" => new Gateway("complex", attrv("@gatewayDirection"))
      case "parallelGateway" => new Gateway("parallel", attrv("@gatewayDirection"))
      case "sequenceFlow" => new SequenceFlow(attrv("@id"), attrv("@sourceRef"), attrv("@targetRef"))
      case _ => None
    }
  }

  def checkNodePath(_node: _Node): Any = {
    _node match {
      case n: StartEvent => {
        if (n.froms.size != 0) "开始节点入口连接数不为零。"
        else if (n.tos.size != 1) "开始节点出口连接数不为1。"
        else None
      }
      case n: EndEvent =>
        {
          if (n.froms.size != 1) "结束节点入口连接数不为1:"
          else if (n.tos.size != 0) "开始节点出口连接数不为零:"
          else None
        }
      case n: Gateway => {
        if (n.direction == "Converging") {
          if (n.froms.size < 1) "聚合节点入口连接数为零"
          else if (n.tos.size != 1) "聚合节点出口连接数不为1"
          else None
        } else if (n.direction == "Diverging") {
          if (n.froms.size != 1) "聚合节点入口连接数不为1"
          else if (n.tos.size <= 0) "聚合节点出口连接数为零"
          else None 
        } else {
          "未知网关节点"
        }
      }
      case n: _Node => {
        if (n.froms.size != 1) "聚合节点入口连接数不为1"
        else if (n.tos.size != 1) "聚合节点出口连接数不为1"
        else None
      }
      case _ => None
    }

  }

  def ParseProcess(xmlnode: Node): Process = {
    implicit val proc = new Process(ele(xmlnode))
    xmlnode.child.foreach { implicit child =>
      val result = parse;
      result match {
        case _n: SequenceFlow => proc.flows += _n.asInstanceOf[SequenceFlow]
        case _n: _Node => proc.nodes += attrv("@id")(child) -> _n.asInstanceOf[_Node]
        case None =>
        case _ => println("unknow==" + result);
      }
    }
    proc.flows.foreach({ seq =>
      val source = proc.nodes.get(seq.sourceId).orNull
      val target = proc.nodes.get(seq.targetId).orNull
      if (source == null) {
        log.error("线[" + seq + "],未找到源节点")
        proc.isvalid = false
      }
      if (target == null) {
        log.error("线[" + seq + "],未找到目的节点")
        proc.isvalid = false
      }
      source.tos += seq
      target.froms += seq
      seq.sourceRef = source
      seq.targetRef = target
    })
    proc.nodes.foreach { keyVnode =>
      checkNodePath(keyVnode._2) match {
        case warn: String => {
            proc.isvalid = false; 
            proc.parseError  += ("节点[id=" + keyVnode._1+",name="+keyVnode._2.e.name  + "]出错:" + warn);
            log.error("节点[id=" + keyVnode._1+",name="+keyVnode._2.e.name  + "]出错:" + warn)
          }
        case _ =>
      }
    }
    println("pp"+proc.parseError )
    proc
  }

  def fromXML(x: Elem): Process = {
    (x \ "process").foreach { process =>
      return ParseProcess(process)
    };
    null
  }
}

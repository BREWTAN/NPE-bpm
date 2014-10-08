package org.nights.npe.util

import scala.xml.Elem
import scala.xml.Node
import org.nights.npe.po.Definition._
import org.slf4j.LoggerFactory
import scala.xml.XML

object POHelper {
  val log = LoggerFactory.getLogger("POHelper")

  def attrv(key: String)(implicit xmlnode: Node): String = {
    (xmlnode \ key).text
  }
  
   def attrv(key: String,key2:String)(implicit xmlnode: Node): String = {
    val v=(xmlnode \ key).text
    if(v!=null && !v.isEmpty())v
    else{
      attrv(key2)
    }
    
   }
   
   def attrvD(key: String,defaultv:String)(implicit xmlnode: Node): String = {
    val v=(xmlnode \ key).text
    if(v!=null && !v.isEmpty())v
    else  defaultv
  }

  def ele(implicit xmlnode: Node): _ele = {
    new _ele(attrv("@id"), attrv("@name"))
  }

  def parse(implicit xmlnode: Node, process: Process): Any = {
    implicit val _node = ele;
    (xmlnode.label, attrv("@gatewayDirection")) match {
      case ("startEvent", _) => new StartEvent
      case ("callActivity", _) => new CallActivity(attrv("@calledElement"))
      case ("subProcess", _) => new SubProcess(ParseProcess(xmlnode));
      case ("endEvent", _) => process.endNodesCount +=1; new EndEvent
      case ("userTask", _) => UserTask(attrv("@actorId"),attrvD("@priority","0").toInt,attrv("@taskName","@name"))
      case ("scriptTask", _) => new ScriptTask(attrv("script"))
      case ("businessRuleTask", _) => new RuleTask

      case ("parallelGateway", "Converging") => new ANDConverging
      case ("parallelGateway", "Diverging") => new ANDDiverging

      case ("exclusiveGateway", "Converging") => new XORConverging
      case ("exclusiveGateway", "Diverging") => new XORDiverging
      case ("inclusiveGateway", "Diverging") => new ORDiverging
      case ("complexGateway", "Converging") => new ANDConverging
      case ("complexGateway", "Diverging") => new ANDDiverging

      //      val priority: Int = 0,
      //    val xsitype: String = null,
      //    val expression: String = null
      case ("sequenceFlow", _) => new SequenceFlow(attrv("@id"), attrv("@sourceRef"), attrv("@targetRef"),
        (xmlnode \ "conditionExpression" find (_.text != null)),
        attrv("@tns:priority"),
        (xmlnode \ "conditionExpression") \ "@xsi:type" text,
        attrv("conditionExpression"))
      case _ => None
    }
  }

  def checkNodePath(_node: _Node): Any = {
    _node match {
      case n: StartEvent => {
        if (n.froms.size != 0) "开始节点入口连接数不为零,实际为:" + n.froms.size
        else if (n.tos.size != 1) "开始节点出口连接数不为1,实际为:" + n.tos.size
        else None
      }
      case n: EndEvent =>
        {
          if (n.froms.size != 1) "结束节点入口连接数不为1,实际为:" + n.froms.size
          else if (n.tos.size != 0) "开始节点出口连接数不为零,实际为:" + n.tos.size
          else None
        }
      case n: ANDDiverging => matchGateWay(n.direction,n)
      case n: XORDiverging => matchGateWay(n.direction,n)
      case n: ORDiverging => matchGateWay(n.direction,n)
      case n: ANDConverging => matchGateWay(n.direction,n)
      case n: XORConverging => matchGateWay(n.direction,n)

      case n: _Node => {
        if (n.froms.size != 1) "节点入口连接数不为1,实际为:" + n.froms.size
        else if (n.tos.size != 1) "节点出口连接数不为1,实际为:" + n.tos.size
        else None
      }
      case _ => None
    }
  }
  def matchGateWay(direction: String, n: _Node):Any = {
    if (direction == "Converging") {
      if (n.froms.size < 1) "聚合节点入口连接数为零,实际为:" + n.froms.size
      else if (n.tos.size != 1) "聚合节点出口连接数不为1,实际为:" + n.tos.size
      else None
    } else if (direction == "Diverging") {
      if (n.froms.size != 1) "聚合节点入口连接数不为1,实际为:" + n.froms.size
      else if (n.tos.size <= 0) "聚合节点出口连接数为零,实际为:" + n.tos.size
      else None
    } else {
      "未知网关节点"
    }
  }

  def ParseProcess(xmlnode: Node): Process = {
    implicit val proc = new Process(ele(xmlnode))
    proc.xmlBody = xmlnode.toString
    xmlnode.child.foreach { implicit child =>
      val result = parse;
      result match {
        case _n: SequenceFlow => proc.flows += _n
        case _n: StartEvent =>
          proc.nodes += "__start" -> _n; proc.nodes += attrv("@id")(child) -> _n
        case _n: _Node => proc.nodes += attrv("@id")(child) -> _n
        case None =>
        case _ => println("unknow==" + result);
      }
    }
    //    println("proc.nodes=" + proc.nodes)
    proc.flows.foreach({ seq =>
      val source = proc.nodes.getOrElse(seq.sourceId, null)
      val target = proc.nodes.getOrElse(seq.targetId, null)
      if (source == null) {
        log.error("线[" + seq + "],未找到源节点")
        proc.isvalid = false
      } else {
        source.tos += seq
        source.next += target
      }
      if (target == null) {
        log.error("线[" + seq + "],未找到目的节点")
        proc.isvalid = false
      } else {
        target.froms += seq
      }
    })
    proc.nodes.foreach { keyVnode =>
      checkNodePath(keyVnode._2) match {
        case warn: String => {
          proc.isvalid = false;
          proc.parseError += ("节点[id=" + keyVnode._1 + ",name=" + keyVnode._2.e.name + "]出错:" + warn);
          log.error("节点[id=" + keyVnode._1 + ",name=" + keyVnode._2.e.name + "]出错:" + warn)
        }
        case _ =>
      }
    }
    if (!proc.isvalid)
      println("process parse error for @" + proc.id + "," + proc.parseError)
//    else
//      println("process parse Success for @" + proc.id)
    proc
  }

  def fromXML(x: Elem): Process = {
    (x \ "process").foreach { process =>
      return ParseProcess(process)
    };
    null
  }

  def fromXMLFile(filename: String): Process = fromXML(XML.loadFile(filename));

}

package org.nights.npe.utils

import scala.collection.mutable.HashMap
import org.nights.npe.po.Definition._
import scala.collection.mutable.MutableList
import scala.collection.mutable.HashMap

class ProcDefMap {
  val procDefs: HashMap[String, Process] = new HashMap[String, Process]
  def appendDefFile(filename: String) {
    val process: Process = POHelper.fromXMLFile(filename)
    procDefs.put(process.e.id, process)
  }

  def appendDefXML(xml: String) {
    val process: Process = POHelper.fromXML(xml)
    procDefs.put(process.e.id, process)
  }

  def processDef(procDefId: String): Process = procDefs.getOrElse(procDefId, NoneProcess)

  def startDefOn(pdefid: String): StateNodeDef = {
    val p = processDef(pdefid)
    StateNodeDef(p.id, p.startNode.id)
  }

  def nodeDefAt(nid: String)(implicit p: Process): StateNodeDef = StateNodeDef(p.id, nid);

  def checkIntegrity: Boolean = {
    var ret = true;
    procDefs.foreach { keyVal =>
      val p: Process = keyVal._2;
     
      p.nodes.foreach({ nodeKV =>
        if (nodeKV._2.isInstanceOf[CallActivity]) {
          val call = nodeKV._2.asInstanceOf[CallActivity];
          if (call.calledElement == null ||
            !procDefs.contains(call.calledElement)) {
            p.isvalid = false;
            ret = false;
            p.parseError.+=:("调用的子流程不存在:" + call.calledElement)
          }
        }
        nodeKV._2.tos.filter({ f =>
          f.compiled match {
            case e: Exception => true
            case _ => false
          }
        }).map { f =>
          p.isvalid = false;
          ret = false;
          p.parseError.+=:("流程表达式错误:@" + f.id + ",from=" + f.sourceId + ",to=" + f.targetId + ",express=" + f.expression)
        }
      })
       val xsize=p.nodes.filter({nodeKV=>
        nodeKV._2.isInstanceOf[XORDiverging]
      }).size
      val vsize=p.nodes.filter({nodeKV=>
        nodeKV._2.isInstanceOf[XORConverging]
      }).size
      
//      if(xsize!=vsize){
//          p.isvalid = false;
//          ret = false;
//          p.parseError.+=:("流程定义错误，异或分支数不等于异或合并数:" +xsize+","+vsize)
//        
//      }
    }
    ret
  }
}

object ProcDefHelper extends ProcDefMap {

}

case class StateNodeDef(procId: String, nodeid: String) {

  def nextStates(implicit p: Process): MutableList[StateNodeDef] = {
    val node = p.getNode(nodeid)

    val nextThatCanGo = p nextNodesFrom nodeid filter (_.id != null);

    for (n <- nextThatCanGo) yield StateNodeDef(p id, n id)

  }

}

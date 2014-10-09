package org.nights.npe.utils

import scala.collection.mutable.HashMap
import org.nights.npe.po.Definition._
import scala.collection.mutable.MutableList
import scala.collection.mutable.HashMap

object ProcDefHelper {

  val procDefs: HashMap[String, Process] = new HashMap[String, Process]

  def appendDefFile(filename: String) {
    val process: Process = POHelper.fromXMLFile(filename)
    procDefs.put(process.e.id, process)
  }

  def processDef(procDefId: String): Process = procDefs.getOrElse(procDefId, NoneProcess)

  def startDefOn(pdefid: String): StateNodeDef = {
    val p = processDef(pdefid)
    StateNodeDef(p.id, p.startNode.id)
  }

  def nodeDefAt(nid: String)(implicit p: Process): StateNodeDef = StateNodeDef(p.id, nid);

  def checkIntegrity: Boolean = {
	 var ret=true;
    procDefs.foreach { keyVal =>
      val p: Process = keyVal._2;
      p.nodes.foreach({ nodeKV =>
        if (nodeKV._2.isInstanceOf[CallActivity]) {
          val call = nodeKV._2.asInstanceOf[CallActivity];
          if (call.calledElement == null ||
            !procDefs.contains(call.calledElement)) {
            p.isvalid = false;
            ret=false;
            p.parseError.+=:("调用的子流程不存在:" + call.calledElement)
          }
        }
      })
    }
    ret
  }
}

case class StateNodeDef(procId: String, nodeid: String) {

  def nextStates(implicit p: Process): MutableList[StateNodeDef] = {
    val node = p.getNode(nodeid)

    val nextThatCanGo = p nextNodesFrom nodeid filter (_.id != null);

    for (n <- nextThatCanGo) yield StateNodeDef(p id, n id)

  }

}

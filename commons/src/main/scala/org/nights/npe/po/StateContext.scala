package org.nights.npe.po

import scala.collection.mutable.HashMap

import org.nights.npe.utils.ProcDefHelper

/**
 * 任务上下文
 */

trait InterState {
  val v: Int
}

case class InterStateNew(val v: Int = 0) extends InterState
case class InterStateObtain(val v: Int = 1) extends InterState
case class InterStateSubmit(val v: Int = 2) extends InterState
case class InterStateTerminate(val v: Int = 3) extends InterState
case class InterStateHangup(val v: Int = 4) extends InterState

case class ParentContext(val procInstId: String, val procDefId: String, val subDefId: String) {
  override def toString = procInstId + "!@!" + procDefId + "!@!" + subDefId
}

case class StateContext(
  val procInstId: String, //流程实例ID
  val procDefId: String, //流程定义ID
  val taskInstId: String, //任务实例ID
  val taskDefId: String, //任务定义ID
  val taskName: String = "",
  val antecessors: List[ParentContext] = List.empty, //父节点信息
  val internalState: InterState = InterStateNew(), //0->新建任务，1-》任务已经被获取，2-》任务已经提交，3->任务结束
  val prevStateInstIds: List[String] = List.empty, //上一次任务实例ID,
  var isTerminate: Boolean = false, //是否为结束节点
  val nodetype: Int = 0 //0为人工节点，1为引擎节点
  ) extends Serializable {

  def simpleName(): String = {
    val p = ProcDefHelper.procDefs.get(procDefId);
    "StateContext(" + p.get.e.name + "," + p.get.nodes.get(taskDefId).get.e.name + ")"
    //    ""
  }
  def copy(from: StateContext): StateContext = {
    StateContext(from.procInstId, from.procDefId, from.taskInstId, from.taskDefId, from.taskName, from.antecessors, from.internalState, from.prevStateInstIds,
      from.isTerminate, from.nodetype)
  }
  private def asInterState(newIState: InterState): StateContext = {
    StateContext(procInstId, procDefId, taskInstId, taskDefId, taskName, antecessors, newIState, prevStateInstIds,
      isTerminate, nodetype)
  }
  def asObtain(): StateContext = {
    asInterState(InterStateObtain())
  }
  def asHangup(): StateContext = {
    asInterState(InterStateHangup())
  }
  def asNew(): StateContext = {
    asInterState(InterStateNew())
  }
  def asSubmit(): StateContext = {
    asInterState(InterStateSubmit())
  }
  def asTerminate(): StateContext = {
    asInterState(InterStateTerminate())
  }
}

trait PriorityAware {
  def pio: Int
  def role: String
  def rolemark: String
  def id:String
}
case class ContextData(
  val procPIO: Int = 0,
  val taskPIO: Int = 0,
  val rolemark: String = null,
  val startMS: Long = System.currentTimeMillis(),
  val idata1: Option[Int] = null, //integer ,
  val idata2: Option[Int] = null, //integer,
  val strdata1: String = null, //text,
  val strdata2: String = null, //text,
  val fdata1: Option[Float] = null, //float,
  val fdata2: Option[Float] = null, //float,
  val taskcenter: String = null,
  val rootproc: String = null,
  val extra: HashMap[String, Any] = HashMap.empty) {
  def PIO = procPIO + taskPIO;

  def getNotNull(a1:String, a2:String) = {
    a1 match{
      case v if a1 == null => a1
      case _ => a2
    }
  }
   def getNotNone(a1:Option[Int], a2:Option[Int]) = {
    a1 match{
      case Some(v) => a1
      case _ => a2
    }
  }
  def getNotNoneF(a1:Option[Float], a2:Option[Float]) = {
    a1 match{
      case Some(v) => a1
      case _ => a2
    }
  }
  def merge(other: ContextData): ContextData = {
    if (PIO >= other.PIO) {
      ContextData(procPIO, taskPIO, rolemark, startMS, 
          getNotNone(idata1,other.idata1 ), 
          getNotNone(idata2,other.idata2 ), 
          getNotNull(strdata1,other.strdata1), 
          getNotNull(strdata2,other.strdata2), 
          getNotNoneF(fdata1,other.fdata1 ), 
          getNotNoneF(fdata2,other.fdata2 ), 
          taskcenter, rootproc, other.extra.++:(extra))
    } else {
      other.merge(this)
    }
  }
  def withTaskPIO(newPIO:Int):ContextData={
        ContextData(procPIO, newPIO, rolemark, startMS, idata1, idata2, strdata1, strdata2, fdata1, fdata2, taskcenter, rootproc, extra)

  }
  def asHigerPIODData(): ContextData = {
    ContextData(procPIO, taskPIO + 1, rolemark, startMS, idata1, idata2, strdata1, strdata2, fdata1, fdata2, taskcenter, rootproc, extra)
  }
  def asNewProcData(procinstid: String): ContextData = {
    ContextData(procPIO, taskPIO + 1, rolemark, startMS, idata1, idata2, strdata1, strdata2, fdata1, fdata2, taskcenter, procinstid, extra)
  }
}


case class StateContextWithData(val sc: StateContext, val dt: ContextData) extends PriorityAware  {
  def pio = dt.procPIO + dt.taskPIO
  def rolemark = dt.rolemark
  def role = sc.taskName
  def id=sc.taskInstId
}

case class DoneStateContext(
  val state: StateContext,
  val ctxData: ContextData,
  val submitter: String)
  


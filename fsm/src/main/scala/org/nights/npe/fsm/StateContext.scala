package org.nights.npe.fsm

import scala.collection.mutable.HashMap

import org.nights.npe.fsm.defs.ProcDefHelper

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
  val procHops: Int = 0, //节点次数,
  var isTerminate: Boolean = false //是否为结束节点
  ) extends Serializable {

  def simpleName(): String = {
//    val p = ProcDefHelper.procDefs.get(procDefId);
//    "StateContext(" + p.get.e.name + "," + p.get.nodes.get(taskDefId).get.e.name + ")"
    ""
  }
  def copy(from: StateContext): StateContext = {
    StateContext(from.procInstId, from.procDefId, from.taskInstId, from.taskDefId, from.taskName, from.antecessors, from.internalState, from.prevStateInstIds,
      from.procHops, from.isTerminate)
  }
  private def asInterState(newIState: InterState): StateContext = {
    StateContext(procInstId, procDefId, taskInstId, taskDefId, taskName, antecessors, newIState, prevStateInstIds,
      procHops, isTerminate)
  }
  def asObtain(): StateContext = {
    asInterState(InterStateObtain())
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
  val extra: HashMap[String, Any] = HashMap.empty) {
  def PIO=procPIO+taskPIO;
  
  def merge(other: ContextData): ContextData={
    if(PIO>=other.PIO){
      ContextData(procPIO,taskPIO,rolemark,startMS,idata1,idata2,strdata1,strdata2,fdata1,fdata2,other.extra.++:(extra))
    }else{
      other.merge(this)
    }
  }
}
case class StateContextWithData(val sc: StateContext, val dt: ContextData) extends PriorityAware {
  def pio = dt.procPIO + dt.taskPIO
  def rolemark = dt.rolemark
  def role = sc.taskName
}

case class DoneStateContext(
  val state: StateContext,
  val ctxData: ContextData,
  val submitter: String)

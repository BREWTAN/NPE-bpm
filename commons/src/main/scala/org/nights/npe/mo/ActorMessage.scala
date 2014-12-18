package org.nights.npe.mo

import org.nights.npe.po.ContextData
import org.nights.npe.po.DoneStateContext
import org.nights.npe.po.StateContext
import org.nights.npe.po.StateContextWithData

case class ANewProcess(procInstId: String, submitter: String, procDefId: String, data: ContextData)

case class TaskDone(doneState: DoneStateContext)

case class ChangeTaskState(val taskInstId:String,val newState:Int)

case class Obtainer(uid: String = null, role: String = null, center: String = null,filter:String =null)

case class AskNewWork(count: Int, obtainer: Obtainer=null)
 
case class ChangeQueuePIO(taskid:String,newPIO:Int,scwData:StateContextWithData=null)

case class Transition(states: List[StateContext], ctxData: ContextData = null)
 
case class RecycleTasks(tasks:List[StateContextWithData])

case class UpdateStates(state: StateContext, ctxData: ContextData = null)

case class SubmitStates(state: StateContext, submitter: String, ctxData: ContextData = null)

case class GatewayStates(state: StateContext, submitter: String, ctxData: ContextData = null)

case class NewProcess(state: StateContext, submitter: String, ctxData: ContextData = null)

case class ObtainedStates(state: StateContext, ctxData: ContextData, obtainer: Obtainer = null)

case class NoneStateInQueue(obtainer: Obtainer = null)

case class FetchProcessStates(procId: String)


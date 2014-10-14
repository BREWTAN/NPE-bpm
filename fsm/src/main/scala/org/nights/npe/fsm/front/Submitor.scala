package org.nights.npe.fsm.front

import org.nights.npe.fsm.ActorHelper
import org.nights.npe.po.ContextData
import org.nights.npe.po.DoneStateContext
import org.nights.npe.po.StateContext
import org.nights.npe.fsm.StatsCounter
import org.nights.npe.fsm.Tansitionworkers
import org.nights.npe.mo.SubmitStates
import org.nights.npe.utils.ProcDefHelper
import org.nights.npe.po.AskResult
import org.nights.npe.po.Definition.NoneProcess
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.nights.npe.mo.NewProcess
import org.nights.npe.mo.TaskDone
import org.nights.npe.mo.ANewProcess

class Submitor extends Actor with ActorLogging with ActorHelper {

  override def preStart(): Unit = {
        log.info("startup@{}", self)
  }
  override def postStop(): Unit = {
        log.info("shutdown:{}", self)
  }

  def receive = {
    case ANewProcess(procInstId,submitter, procDefId, ctxData) => {
      log.info("new Process,{},{},{}",procInstId,procDefId,ctxData)
      val pDef = ProcDefHelper.processDef(procDefId);
      if (pDef == NoneProcess) {
        sender ! AskResult(-1, null, "Process Definition NOUT FOUND!!")
      } else {
        val startDefNode = ProcDefHelper.startDefOn(procDefId)
        val taskInstId = nextUUID;

        val state = new StateContext(procInstId,
          procDefId,
          taskInstId,
          startDefNode.nodeid)
        
        StatsCounter.newprocs.incrementAndGet(); 
        val subdata=ctxData.asNewProcData(procInstId)
        stateStores ! wrapToPipeMessage(NewProcess(state asSubmit, submitter,subdata), Tansitionworkers(), taskInstId);
        sender ! AskResult(1, procInstId)
          
      }
    }
    case TaskDone(doneState: DoneStateContext) => {
      log.info("TaskDone:{}", doneState);
      StatsCounter.submits .incrementAndGet();

      stateStores ! wrapToPipeMessage(SubmitStates(doneState.state asSubmit,doneState.submitter  , doneState.ctxData.asHigerPIODData), Tansitionworkers(), doneState.state.taskInstId);
    }
    case a@_ => log.error("unknow message::"+a)
  }
}
package org.nights.npe.fsm

import java.util.UUID
import scala.collection.mutable.ListBuffer
import org.nights.npe.utils.ProcDefHelper
import org.nights.npe.utils.StateNodeDef
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import org.nights.npe.fsm.backend.Transition
import org.nights.npe.fsm.backend.UpdateStates
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.actor.ActorRef
import org.nights.npe.po.Definition._
import org.nights.npe.fsm.backend.ConvergingResult
import org.nights.npe.fsm.backend.IncreConverging
import org.nights.npe.fsm.backend.UpdateStates
import org.nights.npe.fsm.cluster.ConvergeClusterListener

class TransitionWorker extends ConvergeClusterListener {

  def receive = {
    case UpdateStates(state, ctxData) => {
      doTransite(state, ctxData)
    }
    case a @ _ => receiveOther(a)

  }
  def doTransite(state: StateContext, ctxData: ContextData, issubproc: Boolean = false): ListBuffer[StateContext] = {

    log.info("transiteGet:{}", state)
    implicit val pd = ProcDefHelper.processDef(state.procDefId)

    val prevIds = state.internalState.v match {
      case 2 => List(state.taskInstId)
      case _ => state.prevStateInstIds
    }

    //1.看看是否需要等待..掠过先

    //3.存储

    //2.生成新的节点
    val nextThatCanGo = pd nextNodesFrom state.taskDefId filter (_.id != null);
    val nextStates: ListBuffer[StateContext] = ListBuffer.empty
    val endStates: ListBuffer[StateContext] = ListBuffer.empty

    for (n <- nextThatCanGo) {
      val nextState = pd.getNode(n.id) match {
        case call: CallActivity => {
          val nextSubProc = StateContext(nextUUID,
            call.calledElement,
            nextUUID,
            ProcDefHelper.startDefOn(call.calledElement).nodeid,
            null,
            ParentContext(state.procInstId, state.procDefId, n.id) :: state.antecessors,
            InterStateNew(),
            prevIds)
          doTransite(nextSubProc, ctxData, true)
        }
        case conv: ANDConverging => {
         sendConverge(ConvergingTrans(UpdateStates(state, ctxData), n.froms.size, n.id));
          null
        }
        case conv: XORConverging => {
//          sconvergers ! ConvergingTrans(UpdateStates(state, ctxData), 1, n.id);
          sendConverge(ConvergingTrans(UpdateStates(state, ctxData), 1, n.id))
          null
        }

        case end: EndEvent if state.antecessors.size == 0 => {
          StateContext(state.procInstId, state.procDefId, nextUUID,
            n.id, n.taskName, List.empty, InterStateNew(), prevIds, state.procHops + 1, true).asTerminate
        }
        case end: EndEvent if state.antecessors.size > 0 => {
//                      log.debug("get End From Subprocess1:"+state+","+state.antecessors+"::")
          val parentDef = state.antecessors.head;

          val ppd = ProcDefHelper.processDef(parentDef.procDefId)
          val parentNextfromCur = ppd.getNode(parentDef.subDefId);

          val nextSubProc = StateContext(parentDef.procInstId, parentDef.procDefId, nextUUID,
            parentNextfromCur.id, n.taskName, state.antecessors.drop(1), InterStateNew(), prevIds, state.procHops + 1)
            
          log.info("transite End From SubprocessEnd:" + state + ",Next=" + nextSubProc)

          doTransite(nextSubProc, ctxData)
        }

        case _ => StateContext(state.procInstId, state.procDefId, nextUUID,
          n.id, n.taskName, state.antecessors, InterStateNew(), prevIds, state.procHops + 1)
      }
      if (nextState != null) {
        nextState match {
          case state: StateContext =>
            if (state.isTerminate) {
              endStates.append(state asTerminate)
              log.info("process:{}/{} has reach a terminate!", state.procDefId, state.procInstId)
            } else {
              if (n.tos.size > 1 || n.froms.size > 1) {
                //converging or diverging
                doTransite(state, ctxData)
              } else {
                nextStates.append(state)
              }
            }
          case list: ListBuffer[StateContext] => {
            nextStates.++=:(list)
          }
          case _ =>
        }

      }
    }

    if (issubproc) {
      nextStates.++=:(endStates)
    } else {
      if (nextStates.size > 0) {
        stateStores ! wrapToPipeMessage(Transition(nextStates.result, ctxData), Queueworkers(), state.taskInstId)
        //        pipeAsk(stateStores, ConsistentHashableEnvelope(Transition(nextStates.result, ctxData), state.procInstId), queueworkers)
      }
      if (endStates.size > 0) {
        stateStores ! wrapToPipeMessage(Transition(endStates.result, ctxData), Terminators(), state.taskInstId)
        //        pipeAsk(stateStores, ConsistentHashableEnvelope(Transition(endStates.result, ctxData), state.procInstId),terminators)
      }

      null
    }
  }

}
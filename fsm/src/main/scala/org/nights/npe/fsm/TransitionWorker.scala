package org.nights.npe.fsm

import scala.collection.mutable.ListBuffer
import org.nights.npe.fsm.cluster.ConvergeClusterListener
import org.nights.npe.mo.Transition
import org.nights.npe.mo.UpdateStates
import org.nights.npe.mo.UpdateStates
import org.nights.npe.po.ContextData
import org.nights.npe.po.Definition._
import org.nights.npe.po.InterStateNew
import org.nights.npe.po.ParentContext
import org.nights.npe.po.StateContext
import org.nights.npe.utils.ProcDefHelper
import org.nights.npe.utils.StateNodeDef
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSelection.toScala
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.mvel2.MVEL
import java.util.HashMap
import org.nights.npe.fsm.backend.BeanTransHelper
import scala.collection.mutable.MutableList
import scala.collection.immutable.List

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

    val nextallNodes = pd nextNodesFrom state.taskDefId filter (_.id != null);
    val curnode = pd.getNode(state.taskDefId);

    val nextThatCanGo = curnode match {
      case xor: XORDiverging =>
        val vars = BeanTransHelper.ContextDataToMap(state, ctxData);
        val follows = curnode.tos.filter({ f =>
          if (f.compiled == null) {
            true
          } else if (!f.compiled.isInstanceOf[Exception]) {
            try {
              MVEL.executeExpression(f.compiled, vars).asInstanceOf[Boolean]
            } catch {
              case e: Exception => {
                log.error("xordiverging execute error::" + e)
                false
              }
            }
          } else {
            log.error("xordiverging unknow compiled expression::" + f.compiled)
            false
          }
        }) map { f =>
          pd.getNode(f.targetId)
        }
        if (follows.size == 0) {
          log.error("分支节点去向失败，找不到适合分支,state=" + state + ",data=" + ctxData)
          MutableList.empty
        } else if (follows.size >1) {
          log.error("分支节点去向失败，分支数量》1,state=" + state + ",data=" + ctxData+",follows="+follows)
          MutableList.empty
        } else {
          follows
        }
      case _ =>
        nextallNodes;
    }
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
//          sendConverge(ConvergingTrans(UpdateStates(state, ctxData), n.froms.size, n.id));
          sendConverge(ConvergingTrans(UpdateStates(StateContext(state.procInstId, state.procDefId, nextUUID,
        		  n.id, n.taskName+("(AndConverge自动)"), state.antecessors, InterStateNew(), prevIds,false, 1), ctxData), n.froms.size, n.id))
          null
        }
        case conv: XORConverging => {
          //          sconvergers ! ConvergingTrans(UpdateStates(state, ctxData), 1, n.id);
          sendConverge(ConvergingTrans(UpdateStates(StateContext(state.procInstId, state.procDefId, nextUUID,
        		  n.id, n.taskName+("(xorConverge自动)"), state.antecessors, InterStateNew(), prevIds, false,1), ctxData), 1, n.id))
          null
        }

        case end: EndEvent if state.antecessors.size == 0 => {
          StateContext(state.procInstId, state.procDefId, nextUUID,
            n.id, n.taskName, List.empty, InterStateNew(), prevIds,  true,1).asTerminate
        }
        case end: EndEvent if state.antecessors.size > 0 => {
          //                      log.debug("get End From Subprocess1:"+state+","+state.antecessors+"::")
          val parentDef = state.antecessors.head;

          val ppd = ProcDefHelper.processDef(parentDef.procDefId)
          val parentNextfromCur = ppd.getNode(parentDef.subDefId);

          val nextSubProc = StateContext(parentDef.procInstId, parentDef.procDefId, nextUUID,
            parentNextfromCur.id, n.taskName, state.antecessors.drop(1), InterStateNew(), prevIds, false,1)

          log.info("transite End From SubprocessEnd:" + state + ",Next=" + nextSubProc)

          doTransite(nextSubProc, ctxData)
        }

        case _ => StateContext(state.procInstId, state.procDefId, nextUUID,
          n.id, n.taskName, state.antecessors, InterStateNew(), prevIds, false,
           { if (n.tos.size > 1 || n.froms.size > 1) 1 else 0}
          )
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
          case list: MutableList[StateContext] => {
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
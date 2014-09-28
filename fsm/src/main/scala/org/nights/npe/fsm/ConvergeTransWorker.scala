package org.nights.npe.fsm

import scala.collection.mutable.ListBuffer

import org.nights.npe.fsm.backend.EHConverger
import org.nights.npe.fsm.backend.GatewayStates
import org.nights.npe.fsm.backend.UpdateStates
import org.nights.npe.fsm.defs.ProcDefHelper

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import akka.cluster.Cluster

case class ConvergingTrans(upstate: UpdateStates, convergeCount: Int, convergeNodeId: String)

class ConvergeTransWorker extends Actor with ActorLogging with ActorHelper {
  def addresstoPath(implicit hostport: String): String = {
    "/tmp/fsm/" + hostport.replaceAll(":", "_").replaceAll("@", "@")
  }
  implicit val exec = context.dispatcher

  override def preStart(): Unit = {
    log.info("instanceCacheMan startup@{}", self)
    val storepath = addresstoPath(Cluster(context.system).selfAddress.hostPort)
    log.info("path= startup@{}", storepath)
    EHConverger.initStorage(storepath)
  }
  override def postStop(): Unit = {
    EHConverger.procConverging.getCacheManager().shutdown()
  }

  def receive = {
    case ConvergingTrans(UpdateStates(state, ctxData), convergeCount, convNodeId) => {
      doTransite(state, ctxData, convergeCount, convNodeId)
    }
    case a @ _ => log.error("unknow message::" + a)
  }
  def doTransite(state: StateContext, ctxData: ContextData, convergeCount: Int, convNodeId: String) {
    //    log.info("transiteGet:{}", state)

    val ids = state.internalState.v match {
      case 2 => List(state.taskInstId)
      case _ => state.prevStateInstIds
    }
    val convergeId = state.procInstId + "_" + convNodeId;
    val idsstr = ids.mkString(",")
    val rel = EHConverger.incrAndGetConverging(convergeId, idsstr, ctxData)
    val map = rel._1
    //    if (rel._2) {
    //      store.doSaveConverge(convergeId, ids.mkString(","))
    //    }
    log.info("设置计数器属性@:" + state.procInstId + ",v=" + map + "@" + self)
    if (rel._2 == convergeCount) {
      implicit val pd = ProcDefHelper.processDef(state.procDefId)
      val _node = pd.getNode(state.taskDefId);
      log.info("ANDConverging moving to Next:" + state + ",v=" + map + "@" + self)
      val prevlist: ListBuffer[String] = ListBuffer.empty
      map.foreach({ v =>
        prevlist.++=:(v._1.split(",").toList)
      })

      EHConverger.removeConverging(state.procInstId + "_" + convNodeId)

      val nextstate = StateContext(state.procInstId, state.procDefId, nextUUID,
        _node.next(0).id, state.taskName+"(gateway)", state.antecessors, InterStateSubmit(), prevlist.toList, state.procHops + 1)

      val ctxData = map.foldLeft(ContextData(-1, -1)) { (data, kv) =>
        data.merge(kv._2)
      }
      stateStores ! wrapToPipeMessage(GatewayStates(nextstate, self.toString, ctxData), Tansitionworkers(), nextstate.taskInstId);

    } else {
      log.info("ANDConverging ignore:" + state + ",v=" + map)
      // need to update 
      return
    }

  }

}
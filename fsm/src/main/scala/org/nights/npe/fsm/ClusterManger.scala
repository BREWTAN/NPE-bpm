package org.nights.npe.fsm

import akka.actor.Actor
import akka.actor.ActorLogging
import org.nights.npe.fsm.backend.MySqlStorage
import org.nights.npe.mo.UpdateStates
import org.nights.npe.fsm.backend.EHConverger
import org.nights.npe.mo.Transition

case class RecoverSubmit(cluster: String)
case class RecoverQueue(cluster: String)

class RecoveryWorker extends Actor with ActorLogging with ActorHelper {

  val queue = GlobalQueue.queue
  //  val queueByRole = QueuesList.queueByRole;

  val store = MySqlStorage

  override def preStart(): Unit = {
    log.info("startup@{}", self)
  }

  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }
  val starttime = System.currentTimeMillis();

  def receive = {
    case RecoverSubmit(cluster) => {
      log.info("RecoverSubmit::{}@{}", cluster, self)
      //dorecover

      var cc1 = 0
      store.recoverForStatus(2, { (state, ctxData) =>
        if (state != null) {
          cc1 += 1;
          tansitionworkers ! UpdateStates(state, ctxData)
        } else {
          log.error("RecoverSubmit.Finished::{}@{}::" + cc1, cluster, self)
          self ! RecoverQueue(cluster)
        }
      })(" and createtime <= " + starttime + " order by createtime ")
    }
    case RecoverQueue(cluster) => {
      var cc0 = 0
      store.recoverForStatus(0, { (state, ctxData) =>
        if (state != null) {
          log.debug("forword:" + state)
          cc0 += 1
          forword(Transition(List(state), ctxData), Queueworkers(), state.taskInstId)
        } else {
          log.error("RecoverQueue.Finished::{}@{}::" + cc0, cluster, self)
        }
      })(" and createtime <= " + starttime + " order by createtime ")

    }
    case a @ _ => log.debug("Unknow::" + a + "@" + self)
  }
}


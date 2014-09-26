package org.nights.npe.fsm

import scala.actors.threadpool.LinkedBlockingQueue
import org.nights.npe.fsm.backend.ObtainedStates
import org.nights.npe.fsm.backend.Transition
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.nights.npe.fsm.defs.ProcDefHelper
import org.nights.npe.po.Definition._
import scala.collection.mutable.Map
import java.util.ArrayList
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._
import java.util.PriorityQueue
import org.nights.npe.queue.AdvancePriorityQueue
import org.nights.npe.queue.RolePIOQueue
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.nights.npe.fsm.backend.MySqlStorage

case class AskNewWork(count: Int, obtainer: String = null, role: String = null, group: String = null)

object GlobalQueue {
  val queue: RolePIOQueue[StateContextWithData] = new RolePIOQueue
}
class QueueWorker extends Actor with ActorLogging with ActorHelper {

  val queue = GlobalQueue.queue
  //  val queueByRole = QueuesList.queueByRole;

  override def preStart(): Unit = {
    log.info("startup@{}", self)
  }

  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case Transition(states, ctxData) => {
      log.info("queuestate::{}@{}", states, self)

      for (state <- states) {
        val p = ProcDefHelper.procDefs.get(state.procDefId);
        val node = p.get.getNode(state.taskDefId);
        if (node.isInstanceOf[UserTask]) {
          val usertask = node.asInstanceOf[UserTask];
          queue.offer(StateContextWithData(state, ctxData));
        }
      }
    }
    case AskNewWork(size, obtainer, role, group) => {
      queue.poll(role, obtainer) match {
        case Some(task) => stateStores ! wrapToPipeMessage(ObtainedStates(task.sc asObtain, obtainer, task.dt), sender, task.sc.taskInstId)
        case a @ _ =>
      }
    }
    case a @ _ => log.debug("Unknow::" + a + "@" + self)
  }
}

class ObtainTimeOutChecker extends Actor with ActorLogging with ActorHelper {

  val sc = context.system.scheduler.schedule(2000 millis, 10000 millis, self, "tick")
  override def preStart(): Unit = {
    log.info("startup@{}", self)
  }
  override def postStop(): Unit = {
    //    log.info("shutdown:{}", self)
    sc.cancel
  }

  val store = MySqlStorage

  def receive = {
    case "tick" =>

      val maxtimeout = System.currentTimeMillis() - 60000

//      store.recoverForStatus(1, { (state, ctxData) =>
//        if (state != null) {
//          log.debug(" Find a timeout:State::" + state)
//          forword(Transition(List(state), ctxData), Queueworkers(), state.taskInstId)
//        } else {
//          log.info("RecoverTimeout.Finished::@{}", self)
//        }
//      })(" and obtaintime < " + maxtimeout + " ")

    case a @ _ => log.debug("Unknow::" + a + "@" + self)
  }
}



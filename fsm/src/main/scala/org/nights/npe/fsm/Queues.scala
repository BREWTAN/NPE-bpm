package org.nights.npe.fsm

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.nights.npe.utils.ProcDefHelper
import org.nights.npe.po.Definition.UserTask
import org.nights.npe.queue.RolePIOQueue
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import org.nights.npe.mo.Transition
import org.nights.npe.mo.AskNewWork
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext
import org.nights.npe.po.StateContextWithData
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.mo.NoneStateInQueue
import org.nights.npe.mo.RecycleTasks
import org.nights.npe.mo.RecycleTasks

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
    case recycle:RecycleTasks => {
      log.info("RecycleTasks::{}@{}", recycle, self)
      stateStores ! wrapToCCPipeMessage(recycle, sender,self, self.toString)
    }
    case AskNewWork(size, obtainer) => {
      //      log.info("AskNewWork@"+sender)
      queue.poll(obtainer) match {
        case Some(task) => stateStores ! wrapToPipeMessage(ObtainedStates(task.sc asObtain, task.dt, obtainer), sender, task.sc.taskInstId)
        case a @ _ => sender ! NoneStateInQueue(obtainer)
      }
    }
    case cc @ CCPipeEnvelope(RecycleTasks(tasks), nextActor,ccActor) => {
      for (task <- tasks) {
        queue.offer(task);
      }
      forword("recycle.ok", nextActor, self.toString)
    }
    case ObtainedStates(state, ctxData, obtainer) => {

      log.info("get a reput state@" + sender)

      queue.offer(StateContextWithData(state asNew, ctxData));
    }
    case a @ _ => log.error("Unknow::" + a + "@" + self)
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



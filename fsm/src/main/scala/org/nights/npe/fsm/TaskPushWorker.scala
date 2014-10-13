package org.nights.npe.fsm

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.nights.npe.fsm.backend.ObtainedStates
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import org.nights.npe.fsm.front.TaskDone
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.Broadcast
import akka.routing.Broadcast
class PushWorker extends Actor with ActorLogging with ActorHelper {

  override def preStart(): Unit = {
//    log.info("startup@{}", self)
    context.system.scheduler.schedule(100 millis, 1000 millis, self, "tick")
  }
  override def postStop(): Unit = {
//    log.info("shutdown:{}", self)
  }

  def receive = {
    case "tick" => {
      //      log.debug("asking task:") 
      queuelocalworkers ! Broadcast(AskNewWork(1, self.toString))
    }
    case ObtainedStates(state, obtainer, ctxData) => {
      log.info("get a new work {},@{}",state, obtainer)
      StatsCounter.obtains .incrementAndGet();
      submitors ! ConsistentHashableEnvelope(TaskDone(
        DoneStateContext(state, ctxData, self.toString())), state.taskInstId)
    }

    case _ => log.error("unknow message")
  }
}
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

class PollWorker extends Actor with ActorLogging with ActorHelper {

  var requestCount = 0;
  val sc=context.system.scheduler.schedule(100 millis, 10 millis, self, "tick")
  override def preStart(): Unit = {
        log.info("startup@{}", self)
    
    
  }
  override def postStop(): Unit = {
    //    log.info("shutdown:{}", self)
    sc.cancel
  }

  def receive = {
    case "tick" => {
      requestCount += 1;
      context.become(listening)
      queueAllworkers ! Broadcast(AskNewWork(1, self.toString))
    }
    case obtain:ObtainedStates => {
      log.error("return a to queue  {},@{}", obtain, sender)
      sender ! obtain
    }

    case _ => log.error("unknow message")
  }
  def listening(): Receive = {
    case ObtainedStates(state, obtainer, ctxData) =>
      {
        log.info("get a new work {},@{}", state, obtainer)
        StatsCounter.obtains.incrementAndGet();
        submitors ! ConsistentHashableEnvelope(TaskDone(
          DoneStateContext(state asSubmit, ctxData, self.toString())), state.taskInstId)
        requestCount -= 1;
        if (requestCount <= 0) {
          context.unbecome();
        }
      }
    case "tick" =>
      {
        {
           queueAllworkers ! Broadcast(AskNewWork(1, self.toString))
        }
        requestCount += 1;
      }

  }
}
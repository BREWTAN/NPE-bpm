package org.nights.npe.fsm

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.nights.npe.mo.ObtainedStates
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import org.nights.npe.mo.TaskDone
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.Broadcast
import org.nights.npe.mo.AskNewWork
import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext
import org.nights.npe.po.DoneStateContext
import org.nights.npe.mo.Obtainer

class PollWorker extends Actor with ActorLogging with ActorHelper {

  var requestCount = 0;
  val sc = context.system.scheduler.schedule(100 millis, 10 millis, self, "tick")
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
      queueAllworkers ! Broadcast(AskNewWork(1, Obtainer(self.toString)))
    }
    case obtain: ObtainedStates => {
      log.error("return a to queue  {},@{}", obtain, sender)
      sender ! obtain
    }

    case _ => log.error("unknow message")
  }
  def listening(): Receive = {
    case ObtainedStates(state, ctxData, obtainer) =>
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
          queueAllworkers ! Broadcast(AskNewWork(1,Obtainer( self.toString)))
        }
        requestCount += 1;
      }

  }
}
package org.nights.npe.fsm.backend

import java.util.concurrent.atomic.AtomicLong
import org.nights.npe.fsm.ActorHelper
import akka.actor.Actor
import akka.actor.ActorLogging
import org.nights.npe.fsm.StatsCounter

object PCCouter{
    val term = new AtomicLong(0)

}
class ProcessTerminator extends Actor with ActorLogging with ActorHelper {


  override def preStart(): Unit = {
//    log.info("startup@{}", self)
  }
  override def postStop(): Unit = {
//    log.info("shutdown:{}", self)
  }

  def receive = {
    case Transition(state, ctxData) => {
      StatsCounter.terminates .incrementAndGet();
       
      val start=ctxData.startMS
      val cost=(System.currentTimeMillis()-start)
      log.debug("getEnd====@{},cost={}", state,cost);
      
      if(StatsCounter.maxCost .get()<cost)StatsCounter.maxCost.set(cost)
      if(StatsCounter.minCost .get()>cost)StatsCounter.minCost.set(cost)
      
      StatsCounter.totalProcCount .incrementAndGet()
      StatsCounter.totalCost.addAndGet(cost);
      
    }
    case _ => log.error("unknow message")
  }
}
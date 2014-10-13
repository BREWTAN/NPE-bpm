package org.nights.npe.fsm

import java.util.concurrent.atomic.AtomicLong
import akka.actor.ActorLogging
import akka.actor.Actor

object StatsCounter {

  val newprocs: AtomicLong = new AtomicLong(0);
  val obtains: AtomicLong = new AtomicLong(0);
  val submits: AtomicLong = new AtomicLong(0);
  val terminates: AtomicLong = new AtomicLong(0);

  val maxCost: AtomicLong = new AtomicLong(Long.MinValue);
  val minCost: AtomicLong = new AtomicLong(Long.MaxValue);

  val totalCost: AtomicLong = new AtomicLong(0);
  val totalProcCount: AtomicLong = new AtomicLong(0);
}

class StatsWorker extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("startup@{}", self)
  }

  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case "stats" =>
      log.info("get Stats@" + sender)
      sender ! List(StatsCounter.newprocs.get(), StatsCounter.obtains.get(), StatsCounter.submits.get(), StatsCounter.terminates.get(),
        StatsCounter.maxCost.get, StatsCounter.minCost.get,
        StatsCounter.totalCost.get, StatsCounter.totalProcCount.get,System.currentTimeMillis()).mkString("[",",","]")
    case "queue" =>
      log.info("get queuestat@"+sender)
      sender ! GlobalQueue.queue .toString
    case _ => log.error("unknow message")
  }

}
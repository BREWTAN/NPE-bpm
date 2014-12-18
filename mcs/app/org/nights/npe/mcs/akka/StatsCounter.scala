package org.nights.npe.mcs.akka

import java.util.concurrent.atomic.AtomicLong
import akka.actor.ActorLogging
import akka.actor.Actor
import java.lang.management.ManagementFactory

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

class MCSStatsWorker extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("MCSStatsWorker......startup@{}", self)
  }

  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case "stats" =>
//      log.info("get Stats@" + sender)
      val runtime = Runtime.getRuntime();
      val mb=1024*1024;
      
      val vminfo="{\"threadcount\":"+ManagementFactory.getThreadMXBean().getThreadCount()+
        ",\"threadtotal\":"+ManagementFactory.getThreadMXBean().getTotalStartedThreadCount()+
        ",\"up\":"+ManagementFactory.getRuntimeMXBean().getUptime()+
        ",\"start\":"+ManagementFactory.getRuntimeMXBean().getStartTime()+
        ",\"vminfo\":\""+ManagementFactory.getRuntimeMXBean().getName()+"-"+
        ManagementFactory.getRuntimeMXBean().getVmVendor() +"-"+
        ManagementFactory.getRuntimeMXBean().getVmVersion()  +"\""+
        ",\"memused\":"+(runtime.totalMemory() - runtime.freeMemory()) / mb+
        ",\"memfree\":"+(runtime.freeMemory()) / mb+
        ",\"memmax\":"+(runtime.maxMemory()) / mb+
        ",\"memtotal\":"+(runtime.totalMemory()) / mb+
        "}"
        ;
      sender ! List(StatsCounter.newprocs.get(), StatsCounter.obtains.get(), StatsCounter.submits.get(), StatsCounter.terminates.get(),
        StatsCounter.maxCost.get, StatsCounter.minCost.get,
        StatsCounter.totalCost.get, StatsCounter.totalProcCount.get,System.currentTimeMillis()).mkString("\"data\":[",",","]")+
         ",\"vminfo\":"+ vminfo
        ;
    case a@_ => log.error("unknow message:"+a)
  }

}
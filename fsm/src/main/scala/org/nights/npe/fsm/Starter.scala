package org.nights.npe.fsm

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.cluster.Cluster
import akka.contrib.pattern.ClusterSingletonManager
import akka.routing.RoundRobinPool
import akka.actor.PoisonPill

object Starter {
  def main(args: Array[String]) {
    val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 2551").withFallback(ConfigFactory.load))
    Cluster(system).registerOnMemberUp {
      val fsm = system.actorOf(akka.actor.Props[FsmActorsController], "fsm");
      println("fsm==" + fsm)
      system.actorOf(ClusterSingletonManager.defaultProps(RoundRobinPool(20).props(akka.actor.Props[ConvergeTransWorker]), "convergers",
        PoisonPill, "compute"), "singleton");

      val cmd = system.actorOf(akka.actor.Props[InlineCmdActor], "cmd");
      Thread.sleep(10000)
      cmd ! RecoverFor("")
    }
    val start = System.currentTimeMillis()

    while (true) {
      Thread.sleep(10 * 1000);
      println("stats: pps=" + StatsCounter.terminates.get() * 1000 / ((System.currentTimeMillis() - start))
        + "/s,newps=" + StatsCounter.newprocs.get() * 1000 / ((System.currentTimeMillis() - start))
        + "/s,submitps=" + StatsCounter.submits.get() * 1000 / ((System.currentTimeMillis() - start))
        + "/s,obtainps=" + StatsCounter.obtains.get() * 1000 / ((System.currentTimeMillis() - start))
        + "/s,cost(max=" + StatsCounter.maxCost.get() / 1000
        + "/s,min=" + StatsCounter.minCost.get() / 1000
        + "/s),news=" + StatsCounter.newprocs.get()
        + ",term=" + StatsCounter.terminates.get()
        + ",submits=" + StatsCounter.submits.get()
        + ",obtains=" + StatsCounter.obtains.get() + ")");
      println("roles:" + GlobalQueue.queue)
    }
  }
}
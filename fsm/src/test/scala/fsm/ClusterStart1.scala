package fsm

import org.nights.npe.fsm.TransitionWorker
import org.nights.npe.fsm.cluster.ClusterWorker
import org.nights.npe.fsm.cluster.SimpleClusterListener
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.PoisonPill
import akka.contrib.pattern.ClusterSingletonManager
import org.nights.npe.fsm.cluster.SingleClusterListener
import akka.cluster.Cluster

object ClusterStart1 {

  def main(args: Array[String]) {

    val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 2551").withFallback(ConfigFactory.load))

    //    val worker = system.actorOf(Props[ClusterWorker], "cpp")

    Cluster(system).registerOnMemberUp {
      val act = system.actorOf(ClusterSingletonManager.defaultProps(Props[ClusterWorker], "cpp",
        PoisonPill, "compute"), "singleton");
      //  
      println("act==" + act);
      val myActor = system.actorOf(Props[SimpleClusterListener], "sclistener")

      //    println("workee"+worker)
      for (i <- 0 to 1000) {
        myActor ! "ping"
        Thread.sleep(1000)
      }

    }

  }
}
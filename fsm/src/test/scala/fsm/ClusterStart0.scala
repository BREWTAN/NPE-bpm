package fsm

import org.nights.npe.fsm.cluster.SimpleClusterListener
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.FromConfig
import org.nights.npe.fsm.cluster.ClusterWorker
import org.nights.npe.fsm.cluster.SingleClusterListener
import akka.contrib.pattern.ClusterSingletonManager
import akka.actor.PoisonPill

object ClusterStart0 {

  def main(args: Array[String]) {

    val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 2550").withFallback(ConfigFactory.load))

    Thread.sleep(10000)
       val act=system.actorOf(ClusterSingletonManager.defaultProps(Props[ClusterWorker], "cpp",
      PoisonPill, "compute"), "singleton");
//    val work = system.actorOf(Props[ClusterWorker], "cpp")
//
//        
    val myActor = system.actorOf(Props[SimpleClusterListener], "sclistener")

    println("act=="+act);
//    val myActor = system.actorOf(Props[SingleClusterListener], "sclistener")

    //    println("workee"+worker)
    for (i <- 0 to 1000) {
      myActor ! "ping"
      Thread.sleep(5000)
    }
    
//    for(i <- 0 to 1000)
//    {
//      myActor ! "ping"
//      Thread.sleep(1000)
//    }
    
  }
}
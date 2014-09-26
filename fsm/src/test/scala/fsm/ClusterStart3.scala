package fsm

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.nights.npe.fsm.TransitionWorker
import akka.actor.Props
import akka.routing.FromConfig
import org.nights.npe.fsm.cluster.SimpleClusterListener
import org.nights.npe.fsm.cluster.ClusterWorker
import akka.contrib.pattern.ClusterSingletonManager
import akka.actor.PoisonPill
import org.nights.npe.fsm.cluster.SingleClusterListener

object ClusterStart3 {

  def main(args: Array[String]) {

    
    
    val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 2553").withFallback(ConfigFactory.load))
    
    Thread.sleep(10000)
//    val work = system.actorOf(Props[ClusterWorker], "cpp")
   val act=system.actorOf(ClusterSingletonManager.defaultProps(Props[ClusterWorker], "cpp",
      PoisonPill, "compute"), "singleton");
    val myActor = system.actorOf(Props[SimpleClusterListener], "sclistener")

//    work ! "ping"
    
//    print("start!!"+myActor+"@"+system)
    		
    //    println("workee"+worker)
    for (i <- 0 to 1000) {
      myActor ! "ping"
      Thread.sleep(5000)
    }

  }
}
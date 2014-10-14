package fsm

import scala.collection.mutable.MutableList
import org.nights.npe.fsm.ActorHelper
import org.nights.npe.fsm.InlineCmdActor
import org.nights.npe.po.StateContext
import org.nights.npe.fsm.backend.EhCacheStorage
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.dispatch.Futures.future
import akka.pattern.Patterns.pipe
import akka.pattern.Patterns.pipe
import org.nights.npe.mo.UpdateStates
import org.nights.npe.mo.FetchProcessStates
import org.nights.npe.fsm.FsmActorsController
import org.nights.npe.po.ContextData
import scala.collection.mutable.Map

object TestFSM {

  def main(args: Array[String]) {
    var systems: MutableList[ActorSystem] = MutableList.empty;
    for (i <- 1 to 1) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i).withFallback(ConfigFactory.load))
      systems += system;

      Cluster(system).registerOnMemberUp {
        val fsm = system.actorOf(akka.actor.Props[FsmActorsController], "fsm");

        val cmd = system.actorOf(akka.actor.Props[InlineCmdActor], "cmd");

        println("fsm==" + fsm)
 
        Thread.sleep(5000)

        val state = new StateContext("instance_2",
          "procdef_1",
          "taskInstId_2",
          "taskDefId_1") 

        cmd ! ("Store",UpdateStates(state,ContextData()))
        
        Thread.sleep(1000);
        
        cmd ! ("Store",FetchProcessStates("instance_2"));
        


      }

    }
    Thread.sleep(100000);
    systems.foreach({ system =>
      system.shutdown
    })
  }

}
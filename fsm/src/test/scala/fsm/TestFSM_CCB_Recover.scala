package fsm

import java.util.UUID
import scala.collection.mutable.MutableList
import org.nights.npe.fsm.CMDSubmit
import org.nights.npe.fsm.ConvergeTransWorker
import org.nights.npe.fsm.FsmActorsController
import org.nights.npe.fsm.InlineCmdActor
import org.nights.npe.fsm.StatsCounter
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.actorRef2Scala 
import akka.cluster.Cluster
import akka.contrib.pattern.ClusterSingletonManager
import akka.routing.RoundRobinPool
import org.nights.npe.po.ContextData
import org.nights.npe.fsm.GlobalQueue
import org.nights.npe.fsm.RecoverFor
import org.nights.npe.utils.MessageHelper

object TestFSM_CCB_Recover {

  def main(args: Array[String]) {
    var systems: MutableList[ActorSystem] = MutableList.empty;
    var counter = 0;
    var sleep = 3000000;
    if (args.length >= 1)
      counter = args(0).toInt
    if (args.length >= 2)
      sleep = args(1).toInt

    println("counter=" + counter + ",sleep=" + sleep)
    for (i <- 1 to 1) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i).withFallback(ConfigFactory.load))
      systems += system;
      Cluster(system).registerOnMemberUp {
        if (i == 1) {
          val fsm = system.actorOf(akka.actor.Props[FsmActorsController], "fsm");
          println("fsm==" + fsm)
        }
        system.actorOf(ClusterSingletonManager.defaultProps(RoundRobinPool(40).props(akka.actor.Props[ConvergeTransWorker]), "convergers",
          PoisonPill, "compute"), "singleton");

        val cmd = system.actorOf(akka.actor.Props[InlineCmdActor], "cmd");

        Thread.sleep(8000)

        cmd ! RecoverFor("*")
        val start = System.currentTimeMillis();

//                cmd ! CMDSubmit(MessageHelper.wrappedANewProcess(UUID.randomUUID().toString(),Thread.currentThread().getName(), "ccb.main", ContextData()))
        //        cmd ! CMDSubmit(MessageHelper.wrappedANewProcess(UUID.randomUUID().toString(), "ccb.main", "MyData"))
        //                cmd ! CMDSubmit(MessageHelper.wrappedANewProcess(UUID.randomUUID().toString(), "ccb.main", "MyData"))

        for (i <- 1 to counter)
          new Thread(new Runnable() {
            def run() {
              while (true) {
                cmd ! CMDSubmit(MessageHelper.wrappedANewProcess(UUID.randomUUID().toString(), Thread.currentThread().getName(), "ccb.main", ContextData()))
                Thread.sleep(sleep)
              }
            }
          }).start()

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

    Thread.sleep(8 * 3600 * 1000);
    systems.foreach({ system =>
      system.shutdown
    })
    System.exit(-1)
  }

}
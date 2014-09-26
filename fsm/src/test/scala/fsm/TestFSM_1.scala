package fsm

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable.MutableList
import org.nights.npe.fsm.FsmActorsController
import org.nights.npe.fsm.InlineCmdActor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.dispatch.Futures.future
import akka.pattern.Patterns.pipe
import org.nights.npe.fsm.CMDSubmit
import org.nights.npe.fsm.MessageHelper
import org.nights.npe.fsm.defs.ProcDefHelper
import org.nights.npe.fsm.backend.ProcDefStorage

object TestFSM_1 {

  def main(args: Array[String]) {
    var systems: MutableList[ActorSystem] = MutableList.empty;
    for (i <- 1 to 1) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i).withFallback(ConfigFactory.load))
      systems += system;

      Cluster(system).registerOnMemberUp {
//        val fsm = system.actorOf(akka.actor.Props[FsmActorsController], "fsm");

        val procdef = system.actorOf(akka.actor.Props[ProcDefStorage], "procdef");

//        println("fsm==" + fsm)

//        Thread.sleep(5000)

        val counter = new AtomicLong(0)
        val start = System.currentTimeMillis();
        
        
//        for(i <- 0 to 1)
//        new Thread(new Runnable() {
//          def run() {
//            while (true) {
//              cmd ! CMDSubmit(MessageHelper.wrappedANewProcess(UUID.randomUUID().toString(), "seq1", "MyData"))
//              counter.incrementAndGet();
//              if (counter.get() % 2000 == 0) {
//                println("commit counter," + counter + ",tps=" +
//                  counter.get() * 1000 / ((System.currentTimeMillis() - start)))
//              }
//              Thread.sleep(1)
//            }
//          }
//        }).start()

      }

    }
        Thread.sleep(5*1000);
        systems.foreach({ system =>
          system.shutdown
        })
        System.exit(-1)
  }

}
package fsm

import scala.collection.mutable.MutableList
import scala.reflect.ClassTag
import scala.reflect.classTag

import org.nights.npe.fsm.FsmActorsController
import org.nights.npe.fsm.InlineCmdActor
import org.nights.npe.fsm.StatsWorker

import com.github.mauricio.async.db.exceptions.DatabaseException
import com.github.mauricio.async.db.mysql.exceptions.MySQLException
import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.cluster.Cluster
import akka.routing.Broadcast
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig

class FsmFakeController extends Actor  {
  val stats=context.actorOf(FromConfig.props(Props[StatsWorker]), "stats")
 def receive = {
    case "stats" => 
    	stats!Broadcast(ConsistentHashableEnvelope("stats","stats"))
    case _ => 
  }
}
object TestViews {

  def main(args: Array[String]) {
    var systems: MutableList[ActorSystem] = MutableList.empty;

    for (i <- 9 to 9) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i)
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [viewer]"))
        .withFallback(ConfigFactory.load))
      systems += system;

      Cluster(system).registerOnMemberUp {
        val fsm = system.actorOf(akka.actor.Props[FsmFakeController], "fsm");

        while (true) {
          fsm !"stats"
          Thread.sleep(2 * 1000)
        }

        Thread.sleep(5000)
      }

    }
    Thread.sleep(1000000);
    systems.foreach({ system =>
      system.shutdown
    })
  }

}
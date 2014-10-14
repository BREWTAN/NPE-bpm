package org.nights.npe.mcs.akka

import scala.collection.mutable.HashSet
import scala.collection.mutable.MutableList
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.TasksDAO
import org.slf4j.LoggerFactory

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberUp
import akka.event.slf4j.Logger
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig
import akka.util.Timeout
import play.api._

class FsmCollector extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  val workerRouter = context.actorOf(FromConfig.props(Props.empty),
    name = "stats")

  override def preStart(): Unit = {
    log.info("preStart::")
    //    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents,
    //      classOf[MemberEvent], classOf[UnreachableMember])
    cluster.subscribe(self, classOf[MemberEvent])
    import scala.concurrent.ExecutionContext.Implicits.global

    context.system.scheduler.schedule(5000 millis, 60000 millis, self, "tick")
  }
  override def postStop(): Unit = cluster.unsubscribe(self)
  implicit val timeout = Timeout(20000)

  def receive = {
    case "tick" => {
      val stats = Global.members.map { mem =>
        val fsm = Global.system.actorSelection(mem + "/user/fsm/stats")
        println("fsm==" + workerRouter)
        //        workerRouter ! ConsistentHashableEnvelope(ConsistentHashableEnvelope("stats", "stats"), "stats")
        val result = Await.result(ask(workerRouter, ConsistentHashableEnvelope(ConsistentHashableEnvelope("stats", "stats"), "stats")), timeout.duration) match {
          case fps: String =>
            fps + "," + mem
          case _ =>
          //          //            (0, 0, 0, 0,0,0,0)
        }
        println("member" + mem + " result==" + result)
        result
      }

      println("rere===" + stats);
      //      Global.statsList.+=(re.asInstanceOf[List[String]].mkString("[",",","]"))
      if (stats.size == Global.members.size) {
        Global.statsList += stats
      }
      if (Global.statsList.size > 20) {
        Global.statsList.drop(1)
      }
      //      println("tick.resultGlobal==" + Global.statsList)

    }
    case state: CurrentClusterState ⇒
      log.info("state is Up: " + state)
      Global.members.clear
      state.members.filter(_.roles.contains("compute")).foreach { mem =>
        log.info("currentMember:" + mem.address.toString)
        Global.members.+=(mem.address.toString)
      }

    case MemberRemoved(member, previousStatus) =>
      {
        log.info("Member is Removed: " + member.address + " after " + previousStatus + "")
        if (member.hasRole("compute")) {
          Global.members.remove(member.address.toString);
        }
      }
    case MemberUp(member) => {
      log.info("Member is Up: " + member.address)
      Global.members.add(member.address.toString);
    }
    case _ =>
  }
}

object Global extends GlobalSettings {
  val log = LoggerFactory.getLogger(Global.getClass())
  val members: HashSet[String] = HashSet.empty
  val statsList: MutableList[Any] = MutableList.empty

  val system = ActorSystem.create("PECluster", ConfigFactory.load().getConfig("master"))
  
  val reg = Cluster(system).registerOnMemberUp {
    log.info("member UPUP...");
    val con = system.actorOf(akka.actor.Props[FsmCollector], "fsmcollects")
    log.info("con==" + con)
    val poller=system.actorOf(akka.actor.Props[PollWorker], "poll");
    val submitor=system.actorOf(akka.actor.Props[SubmitWorker], "submitor");

    log.info("poller.create==" + poller)
  }

  override def onStart(app: Application) {
    log.info("Application has started");
    ProcDefDAO
    TasksDAO

  }

  override def onStop(app: Application) {
    log.info("Application shutdown...");
    system.shutdown
  }
}
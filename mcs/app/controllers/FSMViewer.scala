package controllers

import scala.collection.mutable.HashSet
import scala.collection.mutable.MutableList
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.TasksDAO
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.MemberUp
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.util.Timeout
import play.api.Application
import play.api.GlobalSettings
import play.api.Logger
import play.api.mvc.Action
import play.api.mvc.Controller
import akka.routing.BroadcastGroup
import akka.actor.Props
import org.nights.npe.fsm.StatsWorker
import akka.routing.FromConfig

class FsmCollector extends Actor {
  val cluster = Cluster(context.system)
 val workerRouter = context.actorOf(FromConfig.props(Props.empty),
    name = "stats")
  override def preStart(): Unit = {
    Logger.info("preStart")
    //    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents,
    //      classOf[MemberEvent], classOf[UnreachableMember])
    cluster.subscribe(self, classOf[MemberEvent])
    context.system.scheduler.schedule(5000 millis, 5000 millis, self, "tick")
  }
  override def postStop(): Unit = cluster.unsubscribe(self)
  implicit val timeout = Timeout(20000)

  def receive = {
    case "tick" => {
      val stats = Global.members.map { mem =>
        val fsm = Global.system.actorSelection(mem + "/user/fsm/stats")
        println("fsm=="+workerRouter)
//        workerRouter ! ConsistentHashableEnvelope(ConsistentHashableEnvelope("stats", "stats"), "stats")
        val result = Await.result(ask(workerRouter,ConsistentHashableEnvelope(ConsistentHashableEnvelope("stats", "stats"), "stats")), timeout.duration) match {
          case fps: String =>
            fps+","+mem
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
      Logger.info("state is Up: " + state)
      Global.members.clear
      state.members.filter(_.roles.contains("compute")).foreach { mem =>
        Logger.info("currentMember:" + mem.address.toString)
        Global.members.+=(mem.address.toString)
      }

    case MemberRemoved(member, previousStatus) =>
      {
        Logger.info("Member is Removed: " + member.address + " after " + previousStatus + "")
        if (member.hasRole("compute")) {
          Global.members.remove(member.address.toString);
        }
      }
    case MemberUp(member) => {
      Logger.info("Member is Up: " + member.address)
      Global.members.add(member.address.toString);
    }
    case _ =>
  }
}

object Global extends GlobalSettings {

  val members: HashSet[String] = HashSet.empty
  val statsList: MutableList[Any] = MutableList.empty

  val system = ActorSystem.create("PECluster", ConfigFactory.load().getConfig("master"))
  val reg = Cluster(system).registerOnMemberUp {
    Logger.info("member UPUP...");
    val con = system.actorOf(akka.actor.Props[FsmCollector], "fsmcollects")
    Logger.info("con==" + con)
  }

  override def onStart(app: Application) {
    Logger.info("Application has started");
    ProcDefDAO
    TasksDAO

  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...");
    system.shutdown
  }
}

object FSMViewer extends Controller {

  def stats = Action {

    //    println("try to ask:" + master)
    //    val resul1t = Await.result(ask(fsm, ConsistentHashableEnvelope("stats", "stats")), timeout.duration)
    Ok("" + Global.statsList.map({ set =>
      set.asInstanceOf[scala.collection.mutable.HashSet[Any]].map({ s1 =>
        val s = s1 match {
          case fps:String =>  fps
        }
        s
      }).mkString("[", ",", "]")
    }).mkString("[", ",", "]"))
  }
}
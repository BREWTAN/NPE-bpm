package org.nights.npe.mcs.akka

import scala.collection.mutable.HashMap
import scala.collection.mutable.MutableList
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
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
import akka.cluster.ClusterEvent.MemberUp
import akka.event.slf4j.Logger
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig
import akka.util.Timeout
import play.api._
import akka.cluster.ClusterEvent.MemberEvent

class FsmCollector extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    log.info("::fsm start")
    //    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents,
    //      classOf[MemberEvent], classOf[UnreachableMember])

    context.system.actorOf((Props[MCSStatsWorker]), "stats")

    cluster.subscribe(self, classOf[MemberEvent])

    import scala.concurrent.ExecutionContext.Implicits.global

    context.system.scheduler.schedule(5000 millis, 5000 millis, self, "tick")
  }
  override def postStop(): Unit = {
    log.error("::cluster shutdown")
    cluster.unsubscribe(self)
  }
  implicit val timeout = Timeout(5000)
        implicit val ec: ExecutionContext = ExecutionContext.global

  def receive = {
    case "tick" => {
      val stats = Global.members.map { mem =>
        val name = mem._1;
        val status = mem._2;
        
        val computefsm=Global.remote.actorSelection(name.toString()+"/user/stats")
        val viewerfsm=Global.remote.actorSelection(name+"/user/stats")
        println("fsm==" + name + "," + status+",cc="+name.toString()+"/user/stats")
        //        workerRouter ! ConsistentHashableEnvelope(ConsistentHashableEnvelope("stats", "stats"), "stats")
        val result = status match {
          case compute: String if (compute.contains("compute")) =>
            Await.result(ask(computefsm, "stats").recover({case e:java.util.concurrent.TimeoutException => ""}), timeout.duration)
            
            match {
              case fps: String =>
                println("get compute:" + name)

                "{\"fps\":{" + fps + "},\"name\":\"" + name + "\",\"status\":\"" + status + "\"}"
              case _ =>
                println("cannot reach member:" + mem)
                "{\"name\":\"" + name + "\",\"status\":\"" + status + "\"}"
              //          //            (0, 0, 0, 0,0,0,0)
            }
          case viewer: String if (viewer.contains("viewer")) =>
            Await.result(ask(viewerfsm, "stats").recover({case e:java.util.concurrent.TimeoutException => ""}), timeout.duration)
             match {   case fps: String =>
                println("get viewer:" + name)

                "{\"fps\":{" + fps + "},\"name\":\"" + name + "\",\"status\":\"" + status + "\"}"
              case _ =>
                println("cannot reach member:" + mem)
                "{\"name\":\"" + name + "\",\"status\":\"" + status + "\"}"
              //          //            (0, 0, 0, 0,0,0,0)
            }
            

          case _ =>
            "{\"name\":\"" + name + "\",\"status\":\"" + status + "\"}"
        }
        println("result===" + result);
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
      state.members.foreach { mem =>
        log.info("currentMember:" + mem.address.toString)
        Global.members.put(mem.address.toString, "up:" + mem.getRoles)
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
      Global.members.put(member.address.toString, "up:" + member.getRoles);
    }
    case _ =>
  }
}

object Global extends GlobalSettings {
  val log = LoggerFactory.getLogger(Global.getClass())
  val members: HashMap[String, String] = HashMap.empty
  val statsList: MutableList[Any] = MutableList.empty

  val system = ActorSystem.create("PECluster", ConfigFactory.load().getConfig("master"))

    val remote = ActorSystem.create("PERemote", ConfigFactory.load().getConfig("remote"))


  val reg = Cluster(system).registerOnMemberUp {
    log.info("Cluster::member UPUP...");
    val cc = system.actorOf(akka.actor.Props[FsmCollector], "fsmviewer")
    log.info("create fsmcollector==" + cc)
    val poller = system.actorOf(akka.actor.Props[PollWorker], "poll");
    val submitor = system.actorOf(akka.actor.Props[SubmitWorker], "submitor");

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
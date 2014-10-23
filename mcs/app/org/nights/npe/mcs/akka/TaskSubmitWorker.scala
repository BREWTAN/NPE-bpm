package org.nights.npe.mcs.akka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.Broadcast
import com.google.common.cache.AbstractCache.StatsCounter
import org.nights.npe.mo.TaskDone
import org.nights.npe.mo.AskNewWork
import org.nights.npe.po.DoneStateContext
import org.nights.npe.mo.ObtainedStates
import akka.actor.Props
import akka.routing.FromConfig
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.HashMap
import scala.concurrent.Future
import akka.actor.ActorRef
import org.nights.npe.mo.SubmitStates
import org.nights.npe.mo.ANewProcess
import org.nights.npe.mo.ChangeTaskState

class SubmitWorker extends Actor with ActorLogging {

  val askingList: HashMap[String, ConcurrentLinkedQueue[Asker]] = new HashMap()

  val workers = context.actorOf(FromConfig.props(Props.empty),
    name = "workers")

  var requestCount = 0;
  override def preStart(): Unit = {
    log.info("startup.@queu" + workers)

  }
  override def postStop(): Unit = {
    //    sc.cancel
    log.info("stop")
  }

  def receive = {
    case "tick" => {
      requestCount += 1;
    }
    case submit: DoneStateContext =>
      {
        workers.tell(ConsistentHashableEnvelope(ConsistentHashableEnvelope(TaskDone(submit), submit.submitter + "_"), submit.submitter + "_"),sender);
      }
    case newproc: ANewProcess => {
      workers.tell(ConsistentHashableEnvelope(ConsistentHashableEnvelope(newproc, newproc.submitter + "_"), newproc.submitter + "_"),sender);
    }
    case hangup: ChangeTaskState=>{
     workers.tell(ConsistentHashableEnvelope(ConsistentHashableEnvelope(hangup, hangup.taskInstId+ "_"), hangup.taskInstId + "_"),sender);

    }
    case a @ _ => log.error("unknow message::" + a)
  }

}
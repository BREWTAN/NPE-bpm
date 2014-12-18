package org.nights.npe.mcs.akka

import java.util.HashMap
import java.util.concurrent.ConcurrentLinkedQueue
import org.nights.npe.mo.AskNewWork
import org.nights.npe.mo.ObtainedStates
import akka.pattern.ask
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.routing.Broadcast
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig
import org.nights.npe.mo.Obtainer
import org.nights.npe.mo.NoneStateInQueue
import org.nights.npe.mo.Transition
import org.nights.npe.mo.RecycleTasks
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.mo.ChangeQueuePIO
import scala.concurrent.Await
import akka.util.Timeout

object WorkingBuffer {
  val queryByCenterRole: HashMap[String, ConcurrentLinkedQueue[ObtainedStates]] = new HashMap()
}
case class Asker(val address: ActorRef, val exclude: String = null)
class PollWorker extends Actor with ActorLogging {

  val askingList: HashMap[String, ConcurrentLinkedQueue[Asker]] = new HashMap()

  val queueAllworkers = context.actorOf(FromConfig.props(Props.empty),
    name = "queue")

  var requestCount = 0;
  override def preStart(): Unit = {
    log.info("startup.PollWorker@queu" + queueAllworkers)
    //    val sc = context.system.scheduler.schedule(100 millis, 10000 millis, self, "tick")

  }
  override def postStop(): Unit = {
    //    sc.cancel
    log.info("stop")
  }

  def receive = {
    case "tick" => {
      requestCount += 1;
      doQuery(QueryTasks(1))
    }
    case obtain: ObtainedStates =>
      {
        doTaskArrived(obtain, sender)
      }
    case NoneStateInQueue(obtainer) => {
      doTaskArrived(ObtainedStates(null, null, obtainer), sender)
    }
    case rtasks: RecycleTasks => {
      queueAllworkers.tell(ConsistentHashableEnvelope(
        ConsistentHashableEnvelope(rtasks, self.toString), self.toString), sender)
    }
    case query: QueryTasks => {
      doQuery(query)
    }
    case ChangeQueuePIO(taskid, newPIO, scwData) => {
      doChangePIO(taskid, newPIO)
    }

    case a @ _ => log.error("unknow message::" + a)
  }

  def doQuery(query: QueryTasks) {

    val key = query.role match {
      case role: String if role != null => query.center + "_" + query.role + "_" + query.obtainer;
      case _ => query.center + "_" + query.obtainer
    }
    println("doQuery:" + key)
    if (WorkingBuffer.queryByCenterRole.get(key) == null) {
      WorkingBuffer.queryByCenterRole.synchronized {
        if (WorkingBuffer.queryByCenterRole.get(key) == null) {
          WorkingBuffer.queryByCenterRole.put(key, new ConcurrentLinkedQueue())
        }
      }
    }
    val q = WorkingBuffer.queryByCenterRole.get(key).poll()
    if (q != null &&
      //      (query.exclude == null || (!q.ctxData.rolemark.contains(query.exclude)))
      (query.exclude == null || !(q.ctxData != null && q.ctxData.rolemark != null && q.ctxData.rolemark.contains(query.exclude + ",")))) {
      sender ! q
    } else {
      if (!askingList.containsKey(key)) {
        askingList.put(key, new ConcurrentLinkedQueue())
      }
      askingList.get(key).offer(Asker(sender, query.exclude))
      queueAllworkers ! ConsistentHashableEnvelope(
        ConsistentHashableEnvelope(AskNewWork(query.count, Obtainer(query.obtainer, query.role, query.center, query.exclude)), key), key)

    }
    requestCount += 1;
  }

  def doChangePIO(taskid: String, newPIO: Int) = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(60000)
    val result = Await.result(queueAllworkers.ask(ConsistentHashableEnvelope(
      ConsistentHashableEnvelope(ChangeQueuePIO(taskid, newPIO), taskid), taskid)), timeout.duration);
    if (sender != null) {
      sender ! result;
    }

    //      queueAllworkers ! ConsistentHashableEnvelope(
    //    		  ConsistentHashableEnvelope(ChangeQueuePIO(taskid,newPIO),taskid), taskid)
  }

  def doTaskArrived(obtain: ObtainedStates, sender: ActorRef) = {
    log.info("get a new work {},@{}", obtain.state, obtain.obtainer)
    //        StatsCounter.obtains.incrementAndGet();
    //        submitors ! ConsistentHashableEnvelope(TaskDone(
    //          DoneStateContext(state asSubmit, ctxData, self.toString())), state.taskInstId)
    val key = obtain.obtainer match {
      case obtainer: Obtainer if (obtainer.role != null) => obtain.obtainer.center + "_" + obtainer.role + "_" + obtainer.uid
      case _ => obtain.obtainer.center + "_" + obtain.obtainer.uid
    }
    val asker = askingList.get(key).poll()
    if (asker == null) { // || (asker.exclude != null && obtain.ctxData.rolemark!=null&& obtain.ctxData.rolemark.contains(asker.exclude))) {
      sender ! obtain
    } else if (asker.exclude == null || !(obtain.ctxData != null && obtain.ctxData.rolemark != null && obtain.ctxData.rolemark.contains(asker.exclude + ","))) {
      asker.address ! obtain
    } else {
      asker.address ! ObtainedStates(null, null, obtain.obtainer)
    }
    requestCount -= 1;
  }

}
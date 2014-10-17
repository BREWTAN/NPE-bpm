package org.nights.npe.fsm

import scala.concurrent.Await
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.cluster.Cluster
import sun.security.provider.MD5
import java.security.MessageDigest

case class PipeEnvelope(message: Any, nextActor: Any)

case class CCPipeEnvelope(message: Any, nextActor: Any,ccActor:Any)

trait FSMActorSelection {
}
case class StateStores() extends FSMActorSelection
case class Submitors() extends FSMActorSelection
case class Tansitionworkers() extends FSMActorSelection
case class Queueworkers() extends FSMActorSelection
//case class Pushworkers() extends FSMActorSelection
case class Terminators() extends FSMActorSelection

trait ActorHelper { this: Actor ⇒

  def wrapToPipeMessage(message: Any, nextActor: Any, hashkey: String) = ConsistentHashableEnvelope(PipeEnvelope(message, nextActor), hashkey)
    def wrapToCCPipeMessage(message: Any, nextActor: Any,ccActor:Any, hashkey: String) = ConsistentHashableEnvelope(CCPipeEnvelope(message, nextActor,ccActor), hashkey)

  implicit val timeout = Timeout(60000)
  //  import scala.concurrent.ExecutionContext.Implicits.global
  def fsmActorCtrl: ActorSelection = context.actorSelection("/user/fsm")

  lazy val randUUIDFirst = {
   val v=(Cluster(context.system).selfAddress.hostPort).replaceAll("akka://", "").replaceAll("@|:|\\.|PECluster|$", "").toLong.toHexString
   val t=(v + self.path.name.replaceAll("\\$", "") + (Math.random() * 1000000000).asInstanceOf[Int].toHexString);
   t.substring(0,Math.min(t.length()-1,18))
  }

  var inc: Int = 0;

  def nextUUID: String = {
    val time = System.currentTimeMillis()<<13;
    inc += 1;
    inc %= 0xFFFF
    randUUIDFirst+(time + inc).toHexString
  }
  
  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).toString()
}
  def nextStateID(currentId:String,nodeId:String): String = {
    md5(currentId+nodeId)
//    val time = System.currentTimeMillis()<<13;
//    inc += 1;
//    inc %= 0xFFFF
//    randUUIDFirst+(time + inc).toHexString
  }
  //获取持久化的节点
  lazy val stateStores = context.actorSelection("/user/statestores")
  
  
  lazy val stateClusterStores = context.actorSelection("/user/fsm/statestores")

  lazy val submitors = context.actorSelection("/user/submitor")

  lazy val tansitionworkers = context.actorSelection("/user/transitionworker")

  lazy val queuelocalworkers = context.actorSelection("/user/queues")
  lazy val queueAllworkers = context.actorSelection("/user/fsm/queuesall")

//  lazy val pushworkers = context.actorSelection("/user/pushs")
  lazy val terminators = context.actorSelection("/user/terminators")
  lazy val sconvergers = context.actorSelection("/user/singleton/convergers")
  lazy val srecovery = context.actorSelection("/user/singleton-re/recovery")

  def fsmActor: PartialFunction[FSMActorSelection, ActorSelection] = {
    case a: StateStores => stateStores
    case a: Submitors => submitors
    case a: Tansitionworkers => tansitionworkers
    case a: Queueworkers => queuelocalworkers
//    case a: Pushworkers => pushworkers
    case a: Terminators => terminators
    case _ => null
  }
  def syncAsk(target: ActorSelection, message: Any, sender: ActorRef = null): Any = {
    implicit val exec = context.system.dispatchers.lookup("ask-thread-pool-dispatcher")

    val result = Await.result(ask(target, message), timeout.duration);
    if (sender != null) {
      sender ! result;
    }
    return result;
  }

  def forword(message: Any, nextactor: Any, hashKey: String = null) {
    nextactor match {
      case sender: ActorRef => sender ! message
      case selection: FSMActorSelection => fsmActor(selection) ! ConsistentHashableEnvelope(message, hashKey)
      case path: String => context.actorSelection(path) ! message
      case a@_ => println("unknow actor:"+a)
    }
  }

  //  def pipeAsk(target: ActorSelection, message: Any, sender: ActorRef = null) {
  //    pipe(ask(target, message)) to (sender)
  //  }

  //  def pipeAsk(target: ActorSelection, message: Any, sender: ActorSelection) {
  //    pipe(ask(target, message)) to (sender)))))))))))))))
  //  }

}
package org.nights.npe.fsm

import org.nights.npe.fsm.backend.ProcDefStorage
import org.nights.npe.fsm.backend.ProcessTerminator
import org.nights.npe.fsm.backend.StorageWorker
import org.nights.npe.fsm.front.Submitor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.contrib.pattern.ClusterSingletonManager
import akka.routing.FromConfig
import akka.routing.RoundRobinPool

class FsmActorsController extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("startup@{}", self)
    log.info("creatingActor::" + self)
    //---------
    // create EhcacheStorage for statepersistance
    context.actorOf(Props[ProcDefStorage], "definitionstore")
    
    context.actorOf(FromConfig.props(Props[StorageWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "statestores")
   
    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "submitor")

    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "transitionworker")

    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "queues")

    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "queuesall")

//    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "pushs")

    context.actorOf(FromConfig.props.withDispatcher("fsm-thread-pool-dispatcher"), "terminators")

    context.actorOf(FromConfig.props, "stats")
    
    
    val num=20;
//    
    context.system.actorOf(RoundRobinPool(num*2).props(Props[StorageWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "statestores")

    context.system.actorOf(RoundRobinPool(10).props(Props[Submitor]).withDispatcher("fsm-thread-pool-dispatcher"), "submitor")

    context.system.actorOf(RoundRobinPool(num).props(Props[TransitionWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "transitionworker")

    context.system.actorOf(RoundRobinPool(num).props(Props[QueueWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "queues")

    context.system.actorOf(RoundRobinPool(num).props(Props[PushWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "pushs")
//    context.system.actorOf(RoundRobinPool(num).props(Props[PullWorker]).withDispatcher("fsm-thread-pool-dispatcher"), "pulls")

    context.system.actorOf(RoundRobinPool(num).props(Props[ProcessTerminator]).withDispatcher("fsm-thread-pool-dispatcher"), "terminators")

    context.system.actorOf(RoundRobinPool(1).props(Props[StatsWorker]), "stats")
    
    
    context.system.actorOf(ClusterSingletonManager.defaultProps(RoundRobinPool(1).props(akka.actor.Props[RecoveryWorker]), "recovery",
          PoisonPill, "compute"), "singleton-re");

   context.system.actorOf(ClusterSingletonManager.defaultProps(RoundRobinPool(1).props(akka.actor.Props[ObtainTimeOutChecker]), "obtaintimeout",
          PoisonPill, "compute"), "singleton-timeout");
   
  }
  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case "stats" => 
      log.error("get Stats@"+sender)
      sender ! (StatsCounter.newprocs.get(),StatsCounter.obtains .get(),StatsCounter.submits.get(),StatsCounter.terminates .get())

    case _ => log.error("unknow message")
  }
}
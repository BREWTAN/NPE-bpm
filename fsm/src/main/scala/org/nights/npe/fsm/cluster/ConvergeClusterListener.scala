package org.nights.npe.fsm.cluster

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection
import akka.actor.RootActorPath
import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.Member
import akka.contrib.pattern.ClusterSingletonProxy
import akka.pattern.Patterns._
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.nights.npe.fsm.backend.ConvergingResult
import org.nights.npe.fsm.backend.EHConverger
import org.nights.npe.fsm.backend.IncreConverging
import akka.routing.Broadcast
import org.nights.npe.fsm.backend.IncreConvergingFromCluster
import org.nights.npe.fsm.ActorHelper
import org.nights.npe.fsm.ConvergingTrans

class ConvergeClusterListener extends Actor with ActorLogging with ActorHelper {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
  }
  val role = "compute"
  val ageOrdering = Ordering.fromLessThan[Member] { (a, b) ⇒ a.isOlderThan(b) }

  var membersByAge: scala.collection.immutable.SortedSet[Member] = scala.collection.immutable.SortedSet.empty(ageOrdering)

  override def postStop(): Unit = cluster.unsubscribe(self)

  def worker: Option[ActorSelection] =
    membersByAge.headOption map (m ⇒ context.actorSelection(
      RootActorPath(m.address) / "user" / "singleton" / "convergers"))

  def receive = {
    case MemberUp(member) =>
      {
        if (member.hasRole(role)) membersByAge += member
      }
    case state: CurrentClusterState ⇒
      membersByAge = scala.collection.immutable.SortedSet.empty(ageOrdering) ++ state.members.collect {
        case m if m.hasRole(role) ⇒ m
      }

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      {
        log.info("Member is Removed: {} after {}",
          member.address, previousStatus)
        if (member.hasRole(role)) membersByAge -= member

      }
    case _: MemberEvent => // ignore

    case ct: ConvergingTrans => {
      worker foreach (_ ! ct)
      //   context.actorSelection("/usr/fsm/convergers") ! Broadcast(IncreConvergingFromCluster(procInstId, cluster.selfAddress.hostPort))
    }
    

    //      	Patterns.ask(worker!ConsistentHashableEnvelope("ping","ping") 
  }
}
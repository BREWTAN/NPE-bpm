package org.nights.npe.fsm.cluster

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Address
import akka.actor.RootActorPath
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterEvent.LeaderChanged
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.ClusterEvent

class SingleClusterListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    Cluster(context.system).subscribe(self, classOf[LeaderChanged])

  }

  def master: Option[ActorRef] =
    leaderAddress map (a ⇒ context.actorFor(RootActorPath(a) /
      "user" / "singleton" / "cpp"))

  override def postStop(): Unit = cluster.unsubscribe(self)

  //   def worker: Option[ActorSelection] =
  //    membersByAge.headOption map (m ⇒ context.actorSelection(
  //      RootActorPath(m.address) / "user" / "cpp"))
  var leaderAddress: Option[Address] = None

  def receive = {
    case state: CurrentClusterState => leaderAddress = state.leader
    case LeaderChanged(leader) =>
      leaderAddress = leader; log.info("leader is " + leader)
    case other => {
      println("master==" + master)
      master foreach { _ forward other }
    }
  }
}
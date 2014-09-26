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

class SimpleClusterListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    //    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents,
    //      classOf[MemberEvent], classOf[UnreachableMember])
    cluster.subscribe(self, classOf[MemberEvent])

  }
  val role = "compute"
  val ageOrdering = Ordering.fromLessThan[Member] { (a, b) ⇒ a.isOlderThan(b) }

  var membersByAge: scala.collection.immutable.SortedSet[Member] = scala.collection.immutable.SortedSet.empty(ageOrdering)

  override def postStop(): Unit = cluster.unsubscribe(self)
  
  def worker: Option[ActorSelection] =
    membersByAge.headOption map (m ⇒ context.actorSelection(
      RootActorPath(m.address) / "user"/"singleton" / "cpp"))
  
// val worker= cluster.system.actorOf(ClusterSingletonProxy.props("/usr/singleton/cpp",Some("compute")),"cpp");
  
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

    case "ping" => {
      //      implicit val timeout = Timeout(5.seconds)
    	{
//    	  val wokk=worker;
    	  println("worker is "+worker);
    	  worker foreach { _.tell("ping",sender) }
//    	  val kk=cluster.system.actorOf(ClusterSingletonProxy.props("/usr/singleton/cpp",Some("compute")),"consumerProxy");
//    	  println("pingff"+kk);
//    	  worker ! ConsistentHashableEnvelope("ping","ping") 
    	}
    }
    //      	Patterns.ask(worker!ConsistentHashableEnvelope("ping","ping") 
  }
}
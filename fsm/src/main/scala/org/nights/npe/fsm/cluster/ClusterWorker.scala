package org.nights.npe.fsm.cluster

import akka.cluster.ClusterEvent.MemberRemoved
import akka.actor.ActorLogging
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import akka.actor.Actor
import akka.cluster.ClusterEvent

class ClusterWorker extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("preStart@clusterworker");
  }

  def receive = {
    case ping:String => {
      log.info("getPing:"+ping+"@" + self.path+":"+self)
//      sender ! "pong"
    }
    case _ => // ignore
      log.info("unknow:")
  }
}

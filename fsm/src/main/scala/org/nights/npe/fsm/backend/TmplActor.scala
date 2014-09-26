package org.nights.npe.fsm.backend

import akka.actor.ActorLogging
import akka.actor.Actor
import org.nights.npe.fsm.ActorHelper

class TmplActor extends Actor with ActorLogging with ActorHelper{
  
   override def preStart(): Unit = {
    log.info("startup@{}",self)
  }
  override def postStop(): Unit = {
    log.info("shutdown:{}",self)
  }
  
  def receive = {
    case _ =>
  }
}
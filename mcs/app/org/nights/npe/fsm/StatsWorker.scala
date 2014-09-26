package org.nights.npe.fsm

import akka.actor.ActorLogging
import akka.actor.Actor

class StatsWorker extends Actor with ActorLogging {
  
   override def preStart(): Unit = {
    log.info("startup@{}", self)
  }
  
  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

   def receive = {
    case "stats" => 
      log.info("get Stats@"+sender)
      sender ! (0,0,0,0)
    case _ => log.error("unknow message")
  }
   
}
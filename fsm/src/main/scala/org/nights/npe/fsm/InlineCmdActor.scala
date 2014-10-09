package org.nights.npe.fsm

import scala.util.Failure
import org.nights.npe.fsm.front.ANewProcess
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection.toScala
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.nights.npe.backend.db.SimpleDAO
import org.nights.npe.backend.db.DBResult

case class CMDSubmit(cmd: Any)
case class RecoverFor(cluster:String)
class InlineCmdActor extends Actor with ActorLogging with ActorHelper {

  override def preStart(): Unit = {
    log.info("startup@{}", self)
  }
  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  
  def receive = {
    case ("Store", data) => {
      log.info("setState::{}", data)
      val result = syncAsk(stateStores, data)
      log.debug("resut==" + result)
    }
    case CMDSubmit(ConsistentHashableEnvelope(ANewProcess(procInstId,submitter,procDefId,data),key)) => {
      log.info("Submit::{},to{}", ANewProcess(procInstId,submitter,procDefId,data),submitors)
      val result = syncAsk(submitors, ConsistentHashableEnvelope(ANewProcess(procInstId,submitter,procDefId,data),procInstId))
      log.debug("resut==" + result)
    }
    case ("find",dao:SimpleDAO[Any], keyy: String) => {
      log.info("daofind keyy=" + keyy)
      dao.findByKey(keyy) {
        case DBResult(Failure(failure)) => {
        	log.info("failed:"+failure)
        }
        case DBResult(rows: List[Any]) => {
          rows.map({ row =>
            log.info("row=" + row)
          })
        }
      }
      log.info("daofind return=")

      //       Await.result(ask(paramsdao, ("find", keyy)), timeout.duration);
      //      val result = syncAsk(paramsdao ,("find", keyy))
      //      log.info("daofind result="+result)
    }
    case "uuid" =>{
      println(nextUUID)
      println(nextUUID.length())
    }
    case RecoverFor(cluster) => {
      srecovery!RecoverSubmit(cluster)
    }
    case a@_ => log.info("UNknow MESSAGE,{}",a)
  }
}
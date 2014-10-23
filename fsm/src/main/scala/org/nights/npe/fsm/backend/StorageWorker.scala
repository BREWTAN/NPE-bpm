package org.nights.npe.fsm.backend

import scala.concurrent.Future
import scala.util.Success
import org.nights.npe.fsm.ActorHelper
import org.nights.npe.fsm.PipeEnvelope
import org.nights.npe.fsm.CCPipeEnvelope
import org.nights.npe.fsm.Queueworkers
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.mo.Transition
import org.nights.npe.mo.UpdateStates
import org.nights.npe.po.AskResult
import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext
import org.nights.npe.po.StateContextWithData
import com.github.mauricio.async.db.QueryResult
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.cluster.Cluster
import org.nights.npe.mo.GatewayStates
import org.nights.npe.mo.FetchProcessStates
import org.nights.npe.mo.RecycleTasks
import org.nights.npe.mo.NewProcess
import org.nights.npe.mo.SubmitStates
import org.nights.npe.mo.ChangeTaskState
//state op

trait StateStore {
  def initStorage(storepath: String)
  def shutdown()
  def saveTransition(states: List[StateContext], ctxData: ContextData): Future[Any]
  //  def saveUpdateStates(state: StateContext, ctxData: ContextData): Future[Any]
  def doObtainedStates(state: StateContext, obtainer: String): Future[Any]
  def doSubmitStates(state: StateContext, submitter: String, ctxData: ContextData): Future[Any]

  def saveGateway(state: StateContext, submitter: String, ctxData: ContextData): Future[Any];

  def doNewProcess(state: StateContext, submitter: String, ctxData: ContextData): Future[Any]

  def doFetchProcessStates(procId: String): AskResult[Any]

  def doSaveConverge(convergeId: String, taskInsts: String): Future[Any]

  def doRemoveConverge(convergeId: String): Future[Any]

  def doRecycleStates(list: List[StateContextWithData]): Future[Any]
  
  def doUpdateState(taskinstid: String, newstate: Int): Future[Any] ;

}
class StorageWorker extends Actor with ActorLogging with ActorHelper {

  implicit val exec = context.dispatcher

  def addresstoPath(implicit hostport: String): String = {
    "/tmp/fsm/" + hostport.replaceAll("akka://", "_").replaceAll("/", "_")
  }
  //    val store = EhCacheStorage
  val store = MySqlStorage
  //      val store = MySqlInsertStorage

  override def preStart(): Unit = {
    log.info("instanceCacheMan startup@{}", self)
    val storepath = addresstoPath(Cluster(context.system).selfAddress.hostPort)
    log.info("path= {}", storepath)
    store.initStorage(storepath)
  }
  override def postStop(): Unit = {
    //    log.info("instanceCacheMan shutdown:{}", self)
    store.shutdown()
  }

  def receive = {

    case PipeEnvelope(Transition(states, ctxData), nextActor) => {
      log.info("recieveTransition:{}@{}", states, self)
      store.saveTransition(states, ctxData) andThen {
        case _ => forword(Transition(states, ctxData), nextActor, states(0).taskInstId)
      }
    }

    case PipeEnvelope(GatewayStates(state, submitter, ctxData), nextActor) => {
      log.info("recieveGatewayStates:{}@{}", state, self)
      store.saveGateway(state, submitter, ctxData) andThen {
        case _ => forword(UpdateStates(state, ctxData), nextActor, state.procInstId)
      }
    }

    case PipeEnvelope(ObtainedStates(state, ctxData, obtainer), nextActor) => {
      log.info("get ObtainedStates:{}@{},next={}", state, obtainer, nextActor)
      store.doObtainedStates(state, obtainer.uid) andThen {
        //        case _ =>
        //          forword(ObtainedStates(state, obtainer, ctxData), nextActor, state.taskInstId)
        case Success(qr: QueryResult) => {
          if (qr.rowsAffected > 0) {
            log.debug(" obtainer OKOK!!:" + qr)
            forword(ObtainedStates(state, ctxData, obtainer), nextActor, state.taskInstId)
          } else {
            log.error(" obtainer Error!!:Duplicate  obtainer" + qr + "::" + state)
          }
        }
        case a @ _ =>
          {
            log.error(" obtainer Uknow result!!:" + a)
            //            
          }
      }
    }
 
    case PipeEnvelope(SubmitStates(state, submitter, ctxData), nextActor) => {
      log.info("get SubmitStates:{}@{},next={}", state, submitter, nextActor)
      store.doSubmitStates(state, submitter, ctxData) andThen {
        case Success(qr: QueryResult) => {
          if (qr.rowsAffected > 0) {
            log.debug("submit OKOK!!:" + qr)
            forword(UpdateStates(state, ctxData), nextActor, state.taskInstId)
          } else {
            log.error("submit Error!!:Duplicate Submit" + qr + "::" + state)
          }
        }
        case a @ _ =>
          {
            log.error("submit Uknow result!!:" + a)
            //            
          }
      }
    }

    case PipeEnvelope(NewProcess(state, submitter, ctxData), nextActor) => {
      log.info("get SubmitStates:{}@{},next={}", state, submitter, nextActor)
      store.doNewProcess(state, submitter, ctxData) andThen {
        case _ =>
          forword(UpdateStates(state, ctxData), nextActor, state.taskInstId)
      }
    }
    case PipeEnvelope(FetchProcessStates(procId), nextActor) => {
      val rel = store.doFetchProcessStates(procId)
      forword(rel, nextActor, procId)
    }

    case obtain: ObtainedStates => {
      log.info("get reput message:" + obtain)

      forword(obtain, Queueworkers(), obtain.obtainer.uid + "_")
    }
    case cc@CCPipeEnvelope(RecycleTasks(tasks), nextActor,ccActor) => {
      store.doRecycleStates(tasks) andThen {
        case Success(qr: QueryResult) => {
            log.debug("recycle OKOK!!:" + qr)
            forword(cc,ccActor,self.toString)
        }
        case a @ _ =>
          {
            log.error("recycle Uknow result!!:" + a)
            forword("recycyle.error",nextActor,self.toString)
            //            
          }
      }
    }
     case cc@PipeEnvelope(ChangeTaskState(taskinstid,newstate),nextActor) => {
      store.doUpdateState(taskinstid, newstate) andThen {
        case Success(qr: QueryResult) => {
            log.debug("update OKOK!!:" + qr)
            forword(ChangeTaskState(taskinstid,newstate),nextActor,self.toString)
        }
        case a @ _ =>
          {
            log.error("update failed result!!:" + a)
            forword("error",nextActor,self.toString)
            //            
          }
      }
    }
    case a @ _ => {
      log.error("unknow message:" + a)
    }

  }
}
package org.nights.npe.fsm.backend

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.nights.npe.fsm.ActorHelper
import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.PipeEnvelope
import org.nights.npe.fsm.StateContext
import org.nights.npe.fsm.StateContext
import org.nights.npe.po.AskResult
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSelection.toScala
import akka.actor.actorRef2Scala
import akka.cluster.Cluster
import scala.util.Success
import com.github.mauricio.async.db.QueryResult
//state op

case class Transition(states: List[StateContext], ctxData: ContextData = null)

case class UpdateStates(state: StateContext, ctxData: ContextData = null)

case class SubmitStates(state: StateContext, submitter: String, ctxData: ContextData = null)

case class GatewayStates(state: StateContext, submitter: String, ctxData: ContextData = null)

case class NewProcess(state: StateContext, submitter: String, ctxData: ContextData = null)

case class ObtainedStates(state: StateContext, obtainer: String, ctxData: ContextData)

case class FetchProcessStates(procId: String)

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

}
class StorageWorker extends Actor with ActorLogging with ActorHelper {

  implicit val exec = context.dispatcher

  def addresstoPath(implicit hostport: String): String = {
    "/tmp/fsm/" + hostport.replaceAll("akka://", "_").replaceAll("/", "_")
  }
  //  val store = EhCacheStorage
  val store = MySqlStorage
  //    val store = MySqlInsertStorage

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

    case PipeEnvelope(ObtainedStates(state, obtainer, ctxData), nextActor) => {
      log.info("get ObtainedStates:{}@{},next={}", state, obtainer, nextActor)
      store.doObtainedStates(state, obtainer) andThen {
        case _ =>
          forword(ObtainedStates(state, obtainer, ctxData), nextActor, state.taskInstId)
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
            log.error("submit Error!!:Duplicate Submit" + qr+"::"+state)
          }
        }
        case a @ _ =>
          {
            log.debug("submit Uknow result!!:" + a)
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

    case _ => log.error("unknow message")

  }
}
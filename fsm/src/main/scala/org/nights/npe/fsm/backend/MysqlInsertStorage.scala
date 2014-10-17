package org.nights.npe.fsm.backend

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.nights.npe.backend.db.ConvergeDAO
import org.nights.npe.backend.db.InObtainTasksDAO
import org.nights.npe.backend.db.InSubmitTasksDAO
import org.nights.npe.backend.db.InTermTasksDAO
import org.nights.npe.backend.db.KOConvergeCounter
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.ProcinstsDAO
import org.nights.npe.backend.db.TasksDAO
import org.nights.npe.fsm.FSMGlobal
import org.nights.npe.po.AskResult
import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext
import org.nights.npe.utils.BeanTransHelper
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import org.nights.npe.po.StateContextWithData

/**
 * MySqlUpdate的模式
 */
object MySqlInsertStorage extends StateStore {
  val log = LoggerFactory.getLogger("MySqlStorage");
  val hb = new AtomicBoolean(false)
  def initStorage(storepath: String) {
    if (hb.compareAndSet(false, true)) {
      TasksDAO.hb
      ProcinstsDAO.hb
      ProcDefDAO.hb
    }
  }

  def shutdown() = synchronized {
  }
  override def saveTransition(states: List[StateContext], ctxData: ContextData): Future[Any] = {
    //      tx.begin
    //先把当前节点状态进行保存
//    val beans = for (state <- states) yield BeanTransHelper.koFromState(state, ctxData)
//    TasksDAO.insertBatch(beans)
    
        implicit val noexec: Boolean = true;
    val beans = for (state <- states) yield BeanTransHelper.koFromState(state, ctxData)
    val future1 = TasksDAO.insertBatch(beans)
    val prevs = states.foldLeft(List[String]())(_ ++ _.prevStateInstIds).distinct
    val upbeans = prevs.map { BeanTransHelper.koForInTermState(_,"") }
    val future2 = upbeans.map { InTermTasksDAO.insert(_) }
    implicit val exec = FSMGlobal.exec
    val result = Await.ready(Future.sequence(List(future1) ::: future2), 100 seconds)
    log.debug("resultready==" + result.value.get.get)
    TasksDAO.execInBatch(result.value.get.get)

  }

  //  def saveUpdateStates(state: StateContext, ctxData: ContextData): Future[Any] =  { //节点更新，直接保存,主要是节点内部
  //    TasksDAO.insert(BeanTransHelper.koFromState(state, ctxData));
  //  }
  override def doObtainedStates(state: StateContext, obtainer: String): Future[Any] = {
    //    log.trace("get ObtainedStates:@" + state + ",by" + obtainer)
    InObtainTasksDAO.insert(BeanTransHelper.koForObtainState(state, obtainer));
  }
  override def doRecycleStates(list:List[StateContextWithData]): Future[Any] = {
    log.trace("get doRecycleStates:@" )
        implicit val ec: ExecutionContext = ExecutionContext.global

    Future { 1 }
  }

  override def doSubmitStates(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    //    log.trace("get SubmitStates:@" + state + ",by" + submitter)
    InSubmitTasksDAO.insert(BeanTransHelper.koForSubmitState(state, submitter, ctxData));
  }

  override def doNewProcess(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    TasksDAO.insert(BeanTransHelper.koFromNewProcess(state, submitter, ctxData));
  }

  def doFetchProcessStates(procId: String): AskResult[Any] = {
    val tasks: KOTasks = KOTasks(null, null, procId);
    //    log.trace("get FetchProcessStates:" + procId + "@")

    val future = TasksDAO.findByCond(" procinstid='"+procId+"'");

    val timeout = Timeout(60000)

    val result = Await.result(future, timeout.duration);

    AskResult(result.rowsAffected, result.rows, result.statusMessage)
  }
  
 
  override def saveGateway(state: StateContext,submitter:String, ctxData: ContextData): Future[Any] = {
    //      tx.begin
    //先把当前节点状态进行保存
    log.debug("insert new Gateway:::" + state)
//    val bean = BeanTransHelper.koFromState(state,submitter, ctxData)
//    val future1 = TasksDAO.insert(bean)
//    future1
    
    implicit val noexec: Boolean = true;

    val bean = BeanTransHelper.koFromState(state, submitter, ctxData)
    val future1 = TasksDAO.insert(bean)
    val prevs = state.prevStateInstIds.distinct
    val upbeans = prevs.map { BeanTransHelper.koForInTermState(_,"") }
    val future2 = upbeans.map { InTermTasksDAO.insert(_) }
        implicit val exec = FSMGlobal.exec
    //    Future.sequence(List(future1) ::: future2)
    val result = Await.ready(Future.sequence(List(future1) ::: future2), 100 seconds)
    log.debug("resultready==" + result.value.get.get)
    TasksDAO.execInBatch(result.value.get.get)
    
  }

  
  override def doSaveConverge(convergeId: String, taskInsts: String): Future[Any] = {
    ConvergeDAO.insert(KOConvergeCounter(convergeId, taskInsts));
  }
  
  override def doRemoveConverge(convergeId: String): Future[Any] = {
    ConvergeDAO.updateByCond(KOConvergeCounter(null, null,Some(1)), KOConvergeCounter(convergeId))
  }

}
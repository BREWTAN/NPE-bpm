package org.nights.npe.fsm.backend

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Await
import scala.concurrent.Future
import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.StateContext
import org.nights.npe.fsm.backend.db.KOTasks
import org.nights.npe.fsm.backend.db.KOTasks
import org.nights.npe.fsm.backend.db.KOTasks
import org.nights.npe.fsm.backend.db.ObtainTasksDAO
import org.nights.npe.fsm.backend.db.ProcDefDAO
import org.nights.npe.fsm.backend.db.ProcinstsDAO
import org.nights.npe.fsm.backend.db.SubmitTasksDAO
import org.nights.npe.fsm.backend.db.TasksDAO
import org.nights.npe.po.AskResult
import org.nights.npe.po.AskResult
import org.slf4j.LoggerFactory
import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.exceptions.DatabaseException
import akka.util.Timeout
import scala.concurrent.impl.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.impl.Future
import akka.dispatch.ExecutionContexts
import org.nights.npe.fsm.backend.db.KOConvergeCounter
import org.nights.npe.fsm.backend.db.ConvergeDAO
import org.nights.npe.fsm.backend.db.TermUpdateTasksDAO

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
    val beans = for (state <- states) yield BeanTransHelper.koFromState(state, ctxData)
    TasksDAO.insertBatch(beans)

  }

  //  def saveUpdateStates(state: StateContext, ctxData: ContextData): Future[Any] =  { //节点更新，直接保存,主要是节点内部
  //    TasksDAO.insert(BeanTransHelper.koFromState(state, ctxData));
  //  }
  override def doObtainedStates(state: StateContext, obtainer: String): Future[Any] = {
    //    log.trace("get ObtainedStates:@" + state + ",by" + obtainer)
    ObtainTasksDAO.insert(BeanTransHelper.koForObtainState(state, obtainer));
  }
  override def doSubmitStates(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    //    log.trace("get SubmitStates:@" + state + ",by" + submitter)
    SubmitTasksDAO.insert(BeanTransHelper.koForSubmitState(state, submitter, ctxData));
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
    val bean = BeanTransHelper.koFromState(state,submitter, ctxData)
    val future1 = TasksDAO.insert(bean)
    val prevs = state.prevStateInstIds 
    val upbeans = prevs.map { BeanTransHelper.koTermFromString(_) }
    val future2 = upbeans.map { TermUpdateTasksDAO.update(_) }
    implicit val exec = ExecutionContexts.global
    Future.sequence(List(future1) ::: future2)
  }

  
  override def doSaveConverge(convergeId: String, taskInsts: String): Future[Any] = {
    ConvergeDAO.insert(KOConvergeCounter(convergeId, taskInsts));
  }
  
  override def doRemoveConverge(convergeId: String): Future[Any] = {
    ConvergeDAO.updateByCond(KOConvergeCounter(null, null,Some(1)), KOConvergeCounter(convergeId))
  }

}
package org.nights.npe.fsm.backend

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.concurrent.impl.Future
import scala.concurrent.impl.Future
import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.StateContext
import org.nights.npe.fsm.backend.db.ConvergeDAO
import org.nights.npe.fsm.backend.db.DBResult
import org.nights.npe.fsm.backend.db.KO
import org.nights.npe.fsm.backend.db.KOConvergeCounter
import org.nights.npe.fsm.backend.db.KOSubmitTasks
import org.nights.npe.fsm.backend.db.KOTasks
import org.nights.npe.fsm.backend.db.KOTasks
import org.nights.npe.fsm.backend.db.ProcDefDAO
import org.nights.npe.fsm.backend.db.ProcinstsDAO
import org.nights.npe.fsm.backend.db.Range
import org.nights.npe.fsm.backend.db.TasksDAO
import org.nights.npe.fsm.backend.db.TermUpdateTasksDAO
import org.nights.npe.fsm.backend.db.UpdateObtainTasksDAO
import org.nights.npe.fsm.backend.db.UpdateSubmitTasksDAO
import org.nights.npe.po.AskResult
import org.slf4j.LoggerFactory
import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.exceptions.DatabaseException
import akka.dispatch.ExecutionContexts
import akka.util.Timeout
import akka.actor.Status.Success

/**
 * MySqlUpdate的模式
 */
object MySqlStorage extends StateStore {
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
    log.debug("insert new stasks:::" + states)
    implicit val noexec: Boolean = true;
    val beans = for (state <- states) yield BeanTransHelper.koFromState(state, ctxData)
    val future1 = TasksDAO.insertBatch(beans)
    val prevs = states.foldLeft(List[String]())(_ ++ _.prevStateInstIds)
    val upbeans = prevs.map { BeanTransHelper.koTermFromString(_) }
    val future2 = upbeans.map { TermUpdateTasksDAO.update(_) }
    implicit val exec = ExecutionContexts.global
    val result = Await.ready(Future.sequence(List(future1) ::: future2), 100 seconds)
    log.debug("resultready==" + result.value.get.get)
    TasksDAO.execInBatch(result.value.get.get)

  }

  override def saveGateway(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    //      tx.begin
    //先把当前节点状态进行保存
    log.debug("insert new Gateway:::" + state)
    implicit val noexec: Boolean = true;

    val bean = BeanTransHelper.koFromState(state, submitter, ctxData)
    val future1 = TasksDAO.insert(bean)
    val prevs = state.prevStateInstIds
    val upbeans = prevs.map { BeanTransHelper.koTermFromString(_) }
    val future2 = upbeans.map { TermUpdateTasksDAO.update(_) }
    implicit val exec = ExecutionContexts.global
    //    Future.sequence(List(future1) ::: future2)
    val result = Await.ready(Future.sequence(List(future1) ::: future2), 100 seconds)
    log.debug("resultready==" + result.value.get.get)
    TasksDAO.execInBatch(result.value.get.get)
  }

  def saveUpdateStates(state: StateContext, ctxData: ContextData): Future[Any] = { //节点更新，直接保存,主要是节点内部
    TasksDAO.updateSelective(BeanTransHelper.koFromState(state, ctxData));
  }
  override def doObtainedStates(state: StateContext, obtainer: String): Future[Any] = {
    //    log.trace("get ObtainedStates:@" + state + ",by" + obtainer)
    UpdateObtainTasksDAO.updateSelective(BeanTransHelper.koForObtainState(state, obtainer));
  }
  override def doSubmitStates(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    //    log.trace("get SubmitStates:@" + state + ",by" + submitter)
    UpdateSubmitTasksDAO.updateByCond(
      BeanTransHelper.koForSubmitState(state, submitter, ctxData),
      KO.submitFilter(state.taskInstId, 1));
  }

  override def doNewProcess(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    TasksDAO.insert(BeanTransHelper.koFromNewProcess(state, submitter, ctxData));
  }

  override def doFetchProcessStates(procId: String): AskResult[Any] = {
    //    log.trace("get FetchProcessStates:" + procId + "@")

    val future = TasksDAO.findByCond("taskinstid=" + procId);

    val timeout = Timeout(60000)

    val result = Await.result(future, timeout.duration);

    AskResult(result.rowsAffected, result.rows, result.statusMessage)
  }

  override def doSaveConverge(convergeId: String, taskInsts: String): Future[Any] = {
    ConvergeDAO.insert(KOConvergeCounter(convergeId, taskInsts));
  }

  override def doRemoveConverge(convergeId: String): Future[Any] = {
    ConvergeDAO.updateByCond(KOConvergeCounter(null, null, Some(1)), KOConvergeCounter(convergeId))
  }

  def getSubmitTasks(range: Range)(implicit f: DBResult => Unit = null): Future[Any] = {
    TasksDAO.findByCond(" interstate = 2 ", range)
  }

  def recoverForStatus(status: Int, cb: (StateContext, ContextData) => Unit)(implicit cond: String = "") = {
    var offset = 0
    val limit = 100000
    implicit def f(result: DBResult): Unit = {
      result match {
        case DBResult(exception: DatabaseException) => {
          log.debug("failed:" + exception)
        }
        case DBResult(rows: List[Any]) => {
          if (rows.size == 0) {
            log.debug("none result")
            cb(null, null)
          } else {
            rows.map({ row =>
              val rel = BeanTransHelper.koToState(row.asInstanceOf[KOTasks])
              log.debug("rol==" + rel)
              cb(rel._1, rel._2)
            })
            
              offset += rows.size
              TasksDAO.findByCond(" interstate = " + status + cond, Range(offset, limit))
            
          }
          log.error("GetRowSize==" + rows.size+ " status="+status+",cond="+cond+",offset="+offset+",limit="+limit)

        }
        case DBResult(qr: QueryResult) => {
          //          println("qr=" + qr)
        }
        case a @ _ => println("unknow result:" + a)
      }
    }
    TasksDAO.findByCond(" interstate = " + status + cond, Range(offset, limit))
  }

}
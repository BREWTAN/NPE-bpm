package org.nights.npe.fsm.backend

import java.util.HashSet
import scala.concurrent.Future
import scala.concurrent.impl.Future
import scala.concurrent.impl.Future
import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.InterStateNew
import org.nights.npe.fsm.InterStateSubmit
import org.nights.npe.fsm.StateContext
import org.nights.npe.po.AskResult
import org.slf4j.LoggerFactory
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.config.CacheConfiguration
import net.sf.ehcache.config.Configuration
import net.sf.ehcache.store.MemoryStoreEvictionPolicy
import scala.concurrent.ExecutionContext
import scala.concurrent.{ ExecutionContext, Promise }

/**
 * 支持TC超大集群内存模式
 */
object EhCacheStorage extends StateStore {

  implicit val ec: ExecutionContext = ExecutionContext.global
  val log = LoggerFactory.getLogger("EhCacheStorage");
  val stateCache: Cache = new Cache(
    new CacheConfiguration("states", 3000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(false).diskPersistent(false).overflowToDisk(false));

  val procInstCache: Cache = new Cache(
    new CacheConfiguration("procinst", 1000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(false).diskPersistent(false).overflowToDisk(false));

  val procInstDataCache: Cache = new Cache(
    new CacheConfiguration("procinstData", 1000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(false).diskPersistent(false).overflowToDisk(false));

  val stateHistoryCache: Cache = new Cache(
    new CacheConfiguration("statehis", 100).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(false).diskPersistent(false).overflowToDisk(false));

  val procInstHistoryCache: Cache = new Cache(
    new CacheConfiguration("procinsthis", 100).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(false).diskPersistent(false).overflowToDisk(false));

  def initStorage(storepath: String) = synchronized {
    if (stateCache.getCacheManager() == null) {
      val instanceCacheMan = CacheManager.newInstance(new Configuration().name("sc" + storepath))
      instanceCacheMan.addCache(stateCache)
      instanceCacheMan.addCache(stateHistoryCache)
      instanceCacheMan.addCache(procInstCache)
      instanceCacheMan.addCache(procInstHistoryCache)
      instanceCacheMan.addCache(procInstDataCache)
      val list = stateCache.getKeys();

      for (i <- 0 to list.size() - 1) {
        val key = list.get(0)
        val v = stateCache.get(key).getValue().asInstanceOf[StateContext];
        v.internalState match {
          case InterStateNew(r) => {
          }
          case InterStateSubmit(r) => {
          }
          case _ => {
          }
        }
      }
    }
  }
  def shutdown() = synchronized {
    if (stateCache != null && stateCache.getCacheManager() != null) {
      stateCache.dispose();
      stateCache.getCacheManager().shutdown()
    }
  }

  override def saveTransition(states: List[StateContext], ctxData: ContextData): Future[Any] = {
    log.trace("saveTransition:{}@", states)

    //      val tx = stateCache.getCacheManager().getTransactionController()

    var procId: String = null
    var terminated: Boolean = false;
    //      tx.begin
    //先把当前节点状态进行保存
    for (state <- states) {
      if (procId == null) {
        procId = state.procInstId
      }
      for (prevTaskInstId <- state.prevStateInstIds) {
        val oldstate = stateCache.removeAndReturnElement(prevTaskInstId);
        log.trace("delete taskid=" + prevTaskInstId + ",state=" + oldstate)
        stateHistoryCache.put(new Element(prevTaskInstId, oldstate))
      }
      if (!state.isTerminate) {
        stateCache.put(new Element(state.taskInstId, state))
      }
      terminated = (state.isTerminate)
    }

    val last = new HashSet[String]()

    //流程状态保存
    if (terminated) {
      procInstCache.remove(procId)
      procInstDataCache.remove(procId)
      procInstHistoryCache.put(new Element(procId, ctxData))
    } else {
      procInstCache.put(new Element(procId, states))
      if (ctxData != null) {
        procInstDataCache.put(new Element(procId, ctxData))
      }
    }

    Future { 1 }

    //      tx.commit
    //    stateCache flush; stateHistoryCache flush; procInstDataCache flush; procInstCache flush;
    //    if (terminated) procInstHistoryCache flush;
  }

  //  def saveUpdateStates(state: StateContext, ctxData: ContextData): Future[Any] = { //节点更新，直接保存,主要是节点内部
  //    log.trace("get UpdateStates:" + state + "")
  //
  //    stateCache.put(new Element(state.taskInstId, state))
  //    if (state.procHops == 0) {
  //      log.trace("new process!!:@" + state.procInstId)
  //      procInstCache.put(new Element(state.procInstId, state))
  //      //            procInstCache.flush()
  //    }
  //    if (ctxData != null) {
  //      procInstDataCache.put(new Element(state.procInstId, ctxData))
  //      state.internalState match {
  //        case InterStateSubmit(v) => stateHistoryCache.put(new Element(state.taskInstId, ctxData))
  //        case _ =>
  //      }
  //      //      procInstDataCache.flush(); stateHistoyCache.flush()
  //    }
  //    Future { 1 }
  //  }
  override def doObtainedStates(state: StateContext, obtainer: String): Future[Any] = {
    log.trace("get ObtainedStates:@" + state + ",by" + obtainer)
    stateCache.put(new Element(state.taskInstId, state))
    Future { 1 }
  }
  override def doSubmitStates(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    log.trace("get SubmitStates:@" + state + ",by" + submitter)
    stateCache.put(new Element(state.taskInstId, state))
    Future { 1 }
  }
  override def saveGateway(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    stateCache.put(new Element(state.taskInstId, state))
    Future { 1 }
  }
  override def doNewProcess(state: StateContext, submitter: String, ctxData: ContextData): Future[Any] = {
    stateCache.put(new Element(state.taskInstId, state))
    Future { 1 }

  }
  def doFetchProcessStates(procId: String): AskResult[Any] = {
    val ele: Element = procInstCache.get(procId);
    log.trace("get FetchProcessStates:" + procId + "@" + ele)
    if (ele != null) {
      AskResult(1, ele.getValue());
    } else {
      AskResult(0, null)
    }
  }

  override def doSaveConverge(convergeId: String, taskInsts: String): Future[Any] = {
    Future { 1 }
  }
  override def doRemoveConverge(convergeId: String): Future[Any] = {
    Future { 1 }
  }

}
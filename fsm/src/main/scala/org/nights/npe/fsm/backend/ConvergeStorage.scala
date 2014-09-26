package org.nights.npe.fsm.backend

import org.nights.npe.fsm.ContextData
import org.slf4j.LoggerFactory
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.config.CacheConfiguration
import net.sf.ehcache.config.Configuration
import net.sf.ehcache.store.MemoryStoreEvictionPolicy
import scala.collection.mutable.HashMap

//state op

case class IncreConverging(procInstId: String)
case class IncreConvergingFromCluster(procInstId: String, sender: String)
case class ConvergingResult(v: Int)

trait Converger {
  def initStorage(storepath: String)
  def incrAndGetConverging(procInstId: String, stateInstId: String, ctxData: ContextData): (HashMap[String, ContextData],Int);
  def removeConverging(procInstId: String)
}

object EHConverger extends Converger {
  val log = LoggerFactory.getLogger(EHConverger.getClass())
  val procConverging: Cache = new Cache(
    new CacheConfiguration("procconverging", 10000000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU));

  override def initStorage(storepath: String) = synchronized {
    if (procConverging.getCacheManager() == null) {
      val instanceCacheMan = CacheManager.newInstance(new Configuration().name("scconver"))
      instanceCacheMan.addCache(procConverging)
    }
  }

  override def incrAndGetConverging(procInstId: String, stateInstId: String, ctxData: ContextData): (HashMap[String, ContextData],Int) = {
    val v = new HashMap[String, ContextData]();
    if (procConverging.tryWriteLockOnKey(procInstId, 30 * 1000)) {
      if (procConverging.get(procInstId) == null) {
        v.put(stateInstId, ctxData)
        procConverging.put(new Element(procInstId, v))
        procConverging.releaseWriteLockOnKey(procInstId)
        return (v,1)
      } else {
        val v = procConverging.get(procInstId).getValue().asInstanceOf[HashMap[String, ContextData]];
        if (v.put(stateInstId, ctxData) == null) {
          procConverging.put(new Element(procInstId, v));
        }
        val size=v.size
        procConverging.releaseWriteLockOnKey(procInstId)
        return (v,size);
      }
    } else {
      log.error("设置计数器属性失败@:" + procInstId)
      return (v,0);
    }
  }

  override def removeConverging(procInstId: String) = {
    procConverging.remove(procInstId)
  }
}

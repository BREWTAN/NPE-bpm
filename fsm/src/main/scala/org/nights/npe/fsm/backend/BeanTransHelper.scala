package org.nights.npe.fsm.backend

import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext
import org.nights.npe.po.ParentContext
import org.nights.npe.po.InterStateNew
import org.nights.npe.po.InterStateObtain
import org.nights.npe.po.InterStateSubmit
import org.nights.npe.po.InterStateTerminate
import scala.collection.mutable.HashMap
import org.slf4j.LoggerFactory
import org.nights.npe.backend.db.KOTermTask
import org.nights.npe.backend.db.KOObtainTasks
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.KOSubmitTasks
import org.nights.npe.backend.db.KOInTermTask
import org.nights.npe.po.ContextData

object BeanTransHelper {
  val log = LoggerFactory.getLogger(BeanTransHelper.getClass())

  def koFromState(sc: StateContext, dt: ContextData): KOTasks = KOTasks(
    sc.taskInstId, // String, // varchar(32) not null, 
    sc.procDefId, //String=null, // varchar(32) not null,
    sc.procInstId, //String=null, //  varchar(32) not null,
    sc.taskDefId, //String=null, // varchar(32) not null,
    Some(sc.internalState.v), //Option[Int]=null, // int not null default 0,
    sc.prevStateInstIds.mkString("_LIST_"),
    sc.antecessors.mkString("_LIST_"),
    Some(dt.procPIO),
    Some(dt.taskPIO),
    dt.rolemark,
    Some(dt.startMS),
    dt.idata1, //Option[Int]=null, //integer ,
    dt.idata2, //Option[Int]=null, //integer,
    dt.strdata1, //String=null, //text,
    dt.strdata2, //String=null, //text,
    dt.fdata1, //Option[Float]=null, //float,
    dt.fdata2, //Option[Float]=null, //float,
    sc.taskName,
    sc.simpleName, //String=null //longtext null,)
    null,
    null,
    dt.taskcenter,
    dt.rootproc)

  def koFromState(sc: StateContext, submitter: String, dt: ContextData): KOTasks = KOTasks(
    sc.taskInstId, // String, // varchar(32) not null, 
    sc.procDefId, //String=null, // varchar(32) not null,
    sc.procInstId, //String=null, //  varchar(32) not null,
    sc.taskDefId, //String=null, // varchar(32) not null,
    Some(sc.internalState.v), //Option[Int]=null, // int not null default 0,
    sc.prevStateInstIds.mkString("_LIST_"),
    sc.antecessors.mkString("_LIST_"),
    Some(dt.procPIO),
    Some(dt.taskPIO),
    dt.rolemark,
    Some(dt.startMS),
    dt.idata1, //Option[Int]=null, //integer ,
    dt.idata2, //Option[Int]=null, //integer,
    dt.strdata1, //String=null, //text,
    dt.strdata2, //String=null, //text,
    dt.fdata1, //Option[Float]=null, //float,
    dt.fdata2, //Option[Float]=null, //float,
    sc.taskName,
    sc.simpleName, //String=null //longtext null,)
    submitter,
    Some(System.currentTimeMillis()),
    dt.taskcenter,
    dt.rootproc)

  def koToState(ko: KOTasks): (StateContext, ContextData) = (
    StateContext(ko.procinstid, ko.procdefid, ko.taskinstid, ko.taskdefid,
      ko.taskname, ko.antecessors.split("_LIST_").filter(_.length() > 0).map({ v =>
        val spv = v.split("!@!")
        if (spv.size == 3) {
          ParentContext(spv(0), spv(1), spv(2))
        } else {
          log.error("error:::antecessors error::" + v + "::" + spv)
          null
        }
      }).filter(_ != null).toList, ko.interstate match {
        case Some(0) => InterStateNew()
        case Some(1) => InterStateObtain()
        case Some(2) => InterStateSubmit()
        case _ => InterStateTerminate()
      }, ko.previnsts.split("_LIST_").toList),
      ContextData(
        ko.procPIO.get,
        ko.taskPIO.get,
        ko.rolemark,
        ko.startMS.get,
        ko.idata1, //integer ,
        ko.idata2, //integer,
        ko.strdata1, //text,
        ko.strdata2, //text,
        ko.fdata1,
        ko.fdata2, //float,
        ko.taskcenter,
        ko.rootproc,
        // ko.extra: HashMap[String, Any] = 
        HashMap.empty));

  def koTermFromString(taskInstId: String): KOTermTask = KOTermTask(taskInstId)

  def koFromNewProcess(sc: StateContext, submitter: String, dt: ContextData): KOTasks = KOTasks(
    sc.taskInstId, // String, // varchar(32) not null, 
    sc.procDefId, //String=null, // varchar(32) not null,
    sc.procInstId, //String=null, //  varchar(32) not null,
    sc.taskDefId, //String=null, // varchar(32) not null,
    Some(sc.internalState.v), //Option[Int]=null, // int not null default 0,
    sc.prevStateInstIds.mkString("_LIST_"),
    sc.antecessors.mkString("_LIST_"),
    Some(dt.procPIO),
    Some(dt.taskPIO),
    dt.rolemark,
    Some(dt.startMS),
    dt.idata1, //Option[Int]=null, //integer ,
    dt.idata2, //Option[Int]=null, //integer,
    dt.strdata1, //String=null, //text,
    dt.strdata2, //String=null, //text,
    dt.fdata1, //Option[Float]=null, //float,
    dt.fdata2, //Option[Float]=null, //float,
    sc.taskName,
    sc.simpleName, //String=null //longtext null,)
    submitter,
    null,
    dt.taskcenter,
    dt.rootproc)

  def koForObtainState(sc: StateContext, obtainer: String): KOObtainTasks = KOObtainTasks(
    sc.taskInstId, // String, // varchar(32) not null, 
    obtainer)

  def koForInTermState(taskid: String, cluster: String): KOInTermTask = KOInTermTask(
    taskid, // String, // varchar(32) not null, 
    cluster)

  def koForTermState(sc: StateContext): KOTermTask = KOTermTask(
    sc.taskInstId // String, // varchar(32) not null, 
    )
  def koForSubmitState(sc: StateContext, submitter: String, dt: ContextData): KOSubmitTasks = KOSubmitTasks(
    sc.taskInstId, // String, // varchar(32) not null, 
    sc.procDefId,
    sc.procInstId,
    sc.taskDefId,
    submitter,
    Some(InterStateSubmit().v),
    Some(dt.procPIO),
    Some(dt.taskPIO),
    dt.rolemark,
    dt.idata1, //Option[Int]=null, //integer ,
    dt.idata2, //Option[Int]=null, //integer,
    dt.strdata1, //String=null, //text,
    dt.strdata2, //String=null, //text,
    dt.fdata1, //Option[Float]=null, //float,
    dt.fdata2, //Option[Float]=null, //float,
    //    dt.extra.toString //Option[Float]=null, //float,
    sc.simpleName)

    def NullOrZero(o:Option[Any])= {
    if(o==null) 0
    else o.getOrElse(0)
  }
  def ContextDataToMap(sc: StateContext, ctx: ContextData): java.util.HashMap[String, Any] = {
    val ret = new java.util.HashMap[String, Any]
    ret.put("taskName", sc.taskName)
    ret.put("procInstId", sc.procInstId)
    ret.put("taskInstId", sc.taskInstId)
    ret.put("procDefId", sc.procDefId)
    ret.put("taskDefId", sc.taskDefId)
    ret.put("antecessors", sc.antecessors)
    ret.put("idata1", NullOrZero(ctx.idata1))
    ret.put("idata2", NullOrZero(ctx.idata2))
    ret.put("strdata1", ctx.strdata1)
    ret.put("strdata2", ctx.strdata2)
    ret.put("fdata1", NullOrZero(ctx.fdata1))
    ret.put("fdata2", NullOrZero(ctx.fdata2))
    ret.put("taskcenter", ctx.taskcenter)
    ret.put("procPIO", ctx.procPIO)
    ret.put("rolemark", ctx.rolemark)
    ret.put("taskPIO", ctx.taskPIO)
    ret.put("rootproc", ctx.rootproc)
    ret.put("extra", ctx.extra)
    ret
  }
}
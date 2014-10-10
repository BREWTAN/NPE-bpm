package org.nights.npe.fsm.backend

import org.nights.npe.fsm.ContextData
import org.nights.npe.fsm.StateContext
import org.nights.npe.fsm.StateContext
import org.nights.npe.fsm.ParentContext
import org.nights.npe.fsm.InterStateNew
import org.nights.npe.fsm.InterStateObtain
import org.nights.npe.fsm.InterStateSubmit
import org.nights.npe.fsm.InterStateTerminate
import scala.collection.mutable.HashMap
import org.nights.npe.fsm.InterStateSubmit
import org.slf4j.LoggerFactory
import org.nights.npe.backend.db.KOTermTask
import org.nights.npe.backend.db.KOObtainTasks
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.KOSubmitTasks
import org.nights.npe.backend.db.KOInTermTask

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
    dt.taskcenter ,
    dt.rootproc

    )

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
    dt.taskcenter ,
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

}
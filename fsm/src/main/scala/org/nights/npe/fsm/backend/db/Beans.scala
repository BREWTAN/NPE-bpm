package org.nights.npe.fsm.backend.db

import org.nights.npe.fsm.InterStateSubmit
import org.nights.npe.fsm.InterStateSubmit
import org.nights.npe.fsm.InterStateSubmit
import org.nights.npe.fsm.InterStateObtain
import org.nights.npe.fsm.InterStateTerminate

class Beans {

}

case class KOParams(val keyy: String, val valuee: String)

case class KOProcdef(val keyy: String, val jsonbody: String)

case class KOTasks(val taskinstid: String, // varchar(32) not null,
  val procdefid: String = null, // varchar(32) not null,
  val procinstid: String = null, //  varchar(32) not null,
  val taskdefid: String = null, // varchar(32) not null,
  val interstate: Option[Int] = null, // int not null default 0,
  val previnsts: String = null, //text ,
  val antecessors: String = null, //父节点
  val procPIO: Option[Int] = null,
  val taskPIO: Option[Int] = null,
  val rolemark: String = null,
  val startMS: Option[Long] = null,
  val idata1: Option[Int] = null, //integer ,
  val idata2: Option[Int] = null, //integer,
  val strdata1: String = null, //text,
  val strdata2: String = null, //text,
  val fdata1: Option[Float] = null, //float,
  val fdata2: Option[Float] = null, //float,
  val taskname: String = null,
  val jsondata: String = null,
  val submitter: String = null,
  val submittime: Option[Float] = null,
  val createtime: Option[Long] = Some(System.currentTimeMillis()))

case class KOObtainTasks(val taskinstid: String,
  val obtainer: String,
  val obtaintime: Long = System.currentTimeMillis(),
  val interstate: Option[Int] = Some(InterStateObtain().v))

 case class KOInTermTask(val taskinstid: String,
  val cluster: String,
  val calctime: Long = System.currentTimeMillis(),
  val interstate: Option[Int] = Some(InterStateTerminate().v))
  
case class KOSubmitTasks(val taskinstid: String,
  val procdefid: String = null, // varchar(32) not null,
  val procinstid: String = null, //  varchar(32) not null,
  val taskdefid: String = null, // varchar(32) not null,

  val submitter: String,
  val interstate: Option[Int] = Some(InterStateSubmit().v),
  val procPIO: Option[Int] = null,
  val taskPIO: Option[Int] = null,
  val rolemark: String = null,
  val idata1: Option[Int] = null, //integer ,
  val idata2: Option[Int] = null, //integer,
  val strdata1: String = null, //text,
  val strdata2: String = null, //text,
  val fdata1: Option[Float] = null, //float,
  val fdata2: Option[Float] = null, //float,
  val jsondata: String = null,
  val submittime: Option[Long] = Some(System.currentTimeMillis())) {
}
object KO {
  def submitFilter(taskinstid: String, state: Int): KOSubmitTasks = {
    KOSubmitTasks(taskinstid,null, null, null,  null, Some(state), null, null, null, null, null, null, null, null, null, null, null)
  }
}

case class KOTermTask(val taskinstid: String,
  val interstate: Option[Int] = Some(InterStateTerminate().v))

case class KOProcinsts(
  val procinstid: String, //  varchar(32) not null,
  val procdefid: String //  varchar(32) not null,
  );

case class KOConvergeCounter(
  val convergeid: String,
  val taskinstids: String = null,
  val state: Option[Int] = Some(0))

  


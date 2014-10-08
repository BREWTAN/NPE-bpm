package org.nights.npe.fsm.backend.db
trait Countable {
	val __count:Option[Int] = null
}

case class KOParams(val keyy: String, val valuee: String) extends Countable

case class KOProcdef(
  val defid: String = null, 
  val defname: String = null, 
  val version: String = null, 
  val packages: String = null, 
  val xmlbody: String = null, 
  val subelements: String = null, 
  val createtime: Option[Long] = null) extends Countable

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
  val createtime: Option[Long] = Some(System.currentTimeMillis())) extends Countable


  


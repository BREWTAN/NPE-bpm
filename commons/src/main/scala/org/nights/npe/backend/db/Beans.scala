package org.nights.npe.backend.db

class Beans {

}

object InterState {
  val New: Int = 0
  val Obtain: Int = 1
  val Submit: Int = 2
  val Terminate: Int = 3
  val ProcessEnd: Int = 8
}

case class KOParams(val keyy: String, val valuee: String)

case class KOProcdef(
  val defid: String = null,
  val defname: String = null,
  val version: String = null,
  val packages: String = null,
  val xmlbody: String = null,
  val subelements: String = null,
  val createtime: Option[Long] = null)

case class KOProcdefCounter(val __count: Option[Long])

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
  val taskcenter: String = null,
  val rootproc: String = null,
  val nodetype:Option[Int] = Some(0),//节点类型，0表示人工，1表示引擎计算
  val createtime: Option[Long] = Some(System.currentTimeMillis())
)

case class KOObtainTasks(val taskinstid: String,
  val obtainer: String,
  val obtaintime: Long = System.currentTimeMillis(),
  val interstate: Option[Int] = Some(1))

case class KOInTermTask(val taskinstid: String,
  val cluster: String,
  val calctime: Long = System.currentTimeMillis(),
  val interstate: Option[Int] = Some(InterState.Terminate))

case class KOSubmitTasks(val taskinstid: String,
  val procdefid: String = null, // varchar(32) not null,
  val procinstid: String = null, //  varchar(32) not null,
  val taskdefid: String = null, // varchar(32) not null,

  val submitter: String,
  val interstate: Option[Int] = Some(InterState.Submit),
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

case class KOTermProc(
  val procinstid: String = null, //  varchar(32) not null,
  val taskdefid: String = "_1",
  val interstate: Option[Int] = Some(InterState.ProcessEnd),
  val submittime: Option[Long] = Some(System.currentTimeMillis())) {
}

object KO {
  def submitFilter(taskinstid: String, state: Int): KOSubmitTasks = {
    KOSubmitTasks(taskinstid, null, null, null, null, Some(state), null, null, null, null, null, null, null, null, null, null, null)
  }
}

case class KOTermTask(val taskinstid: String,
  val interstate: Option[Int] = Some(InterState.Terminate))

case class KOProcinsts(
  val procinstid: String, //  varchar(32) not null,
  val procdefid: String //  varchar(32) not null,
  );

case class KOConvergeCounter(
  val convergeid: String,
  val taskinstids: String = null,
  val state: Option[Int] = Some(0))

case class KOTaskCenter(
  val centerid: String,
  val centername: String = null,
  val status:Option[Int] =  Some(0),
  val createtime: Option[Long] = Some(System.currentTimeMillis()),
  val modtime: Option[Long] = Some(System.currentTimeMillis()))

case class KOTaskRole(
  val roleid: String,
  val centerid: String,
  val rolename: String = null,
  val status:Option[Int] =  Some(0),
  val createtime: Option[Long] = Some(System.currentTimeMillis()),
  val modtime: Option[Long] = Some(System.currentTimeMillis()))

  


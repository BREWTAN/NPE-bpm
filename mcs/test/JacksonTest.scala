import scala.collection.mutable.HashMap
import org.nights.npe.po.ContextData
import org.nights.npe.po.InterState
import org.nights.npe.po.ParentContext
import org.nights.npe.po.StateContext
import org.nights.npe.po.StateContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import org.nights.npe.po.InterStateNew
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.nights.npe.po.InterStateSubmit
import org.nights.npe.po.InterStateObtain
import org.nights.npe.po.InterStateSubmit
import org.nights.npe.po.InterStateTerminate
import org.nights.npe.po.ContextData
import org.nights.npe.mo.SubmitStates
object JacksonTest {

  import org.nights.npe.mcs.akka.JerksonHelper._
  def main(args: Array[String]) {

    //    val mapper = new ObjectMapper()
    //    mapper.registerModule(DefaultScalaModule)
    //
    //    val sc = StateContext("a", "b", "c", "d", "taskname",
    //      List(ParentContext("p1", "p2", "P3"), ParentContext("p3", "p2", "P3")),
    //      InterStateSubmit(), List("prev1", "prev2"), 100, false)
    //
    //    println("sc=" + sc)
    //    val jsonstr = mapper.writeValueAsString(sc)
    //
    //    println("sc.json==" + jsonstr)
    //
    //    val jerkson = Json.parse(jsonstr);
    //    println("sc.jerkson=" + jerkson);
    //    val sc2 = Json.fromJson[StateContext](jerkson).get;
    //
    //    println("sc2=" + sc2)
    //
    //    val cdata = ContextData(1, 2, "mask", System.currentTimeMillis(), Some(1), Some(2), "str1", "str2", Some(1.0f), Some(2.0f), "center", "root", HashMap("keyv" -> "vv", "keyv2" -> "vv", "keyv3" -> "vv"))
    //
    //    println("cdata=" + cdata)
    //    val cdatastr = mapper.writeValueAsString(cdata)
    //    val cdatajerkson = Json.parse(cdatastr);
    //    println("cdata.jerkson=" + cdatajerkson);
    //
    //    val cdata2 = Json.fromJson[ContextData](cdatajerkson).get;
    //
    //    println("cdata2=" + cdata2)

    val str = """
     {"state":{"procInstId":"bf4ab87d-8225-42a4-8987-f49944ed0ab7","procDefId":"ccb.main","taskInstId":"4bb2da87d1604d662921d33f222001","taskDefId":"_jbpm-unique-0","taskName":"扫描","antecessors":[],"internalState":{"v":1},"prevStateInstIds":["4bb2da87d16021592921d33f208001"],"procHops":1,"isTerminate":false},"ctxData":{"procPIO":3,"taskPIO":1,"rolemark":"","startMS":1413289212163,"idata1":null,"idata2":null,"strdata1":"","strdata2":"","fdata1":null,"fdata2":null,"taskcenter":null,"rootproc":"bf4ab87d-8225-42a4-8987-f49944ed0ab7","extra":{}},"obtainer":{"uid":"undefined","role":null,"center":"undefined","filter":null},"submitter":"undefined"}
      """
    val json = Json.parse(str)
    println("json=" + json)
    val ss = Json.fromJson[SubmitStates](json)
    println("ss=" + ss)

    val submitContextDataReads: Reads[ContextData] = (
     (__ \ "procPIO").read[Int] and
    (__ \ "taskPIO").read[Int] and
    (__ \ "rolemark").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "startMS").readNullable[Long].map({_.getOrElse(System.currentTimeMillis())}) and
    (__ \ "idata1").read[Option[Int]] and
    (__ \ "idata2").read[Option[Int]] and
    (__ \ "strdata1").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "strdata2").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "fdata1").read[Option[Float]] and
    (__ \ "fdata2").read[Option[Float]] and
    (__ \ "taskcenter").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "rootproc").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "extra").read[HashMap[String, Any]])(ContextData.apply _);
    
    
    {
      val ds = """
        {"strdata1":"","strdata2":"","idata1":1,"idata2":2,"fdata1":1,"fdata2":2,"taskPIO":0,"procPIO":0,"taskcenter":null,"startMS":null,"rootproc":null,"extra":{}
       } 
        """
      val json = Json.parse(ds)
      println("json=" + json)
      val ss = Json.fromJson[ContextData](json)(submitContextDataReads)
      println("kkk:" + ss)
    }

  }

}

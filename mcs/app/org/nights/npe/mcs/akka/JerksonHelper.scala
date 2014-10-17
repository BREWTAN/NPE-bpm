package org.nights.npe.mcs.akka
import scala.collection.mutable.HashMap
import org.nights.npe.po.ContextData
import org.nights.npe.po.InterState
import org.nights.npe.po.ParentContext
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
import org.nights.npe.po.InterStateTerminate
import org.nights.npe.po.ContextData
import org.nights.npe.mo.SubmitStates
import org.nights.npe.po.DoneStateContext

object JerksonHelper {

  implicit val ParentContextReads: Reads[ParentContext] = (
    (JsPath \ "procInstId").readNullable[String].map({ _.getOrElse(null) }) and
    (JsPath \ "procDefId").readNullable[String].map({ _.getOrElse(null) }) and
    (JsPath \ "subDefId").readNullable[String].map({ _.getOrElse(null) }))(ParentContext.apply _)

  case class InterStateR(val v: Int) extends InterState

  implicit val objectMapFormat = new Format[HashMap[String, Any]] {
    def reads(jv: JsValue): JsResult[HashMap[String, Any]] =
      {
        val jo = jv.as[JsObject]
        val map: HashMap[String, Any] = HashMap.empty
        jo.fields.map({ field =>
          map.put(field._1.asInstanceOf[String], field._2.as[String])
        })
        JsSuccess(map)
      }
    def writes(map: HashMap[String, Any]): JsValue =
      Json.obj(
        "val1" -> map("val1").asInstanceOf[String],
        "val2" -> map("val2").asInstanceOf[String])

  }
  implicit val InterStateReads: Reads[InterState] = (__ \ "v").read[Int].map { v =>
    v match {
      case 0 => InterStateNew()
      case 1 => InterStateObtain()
      case 2 => InterStateSubmit()
      case 3 => InterStateTerminate()
      case _ => InterStateTerminate(v)
    }

  }

  implicit val StateContextReads: Reads[StateContext] = (
    (__ \ "procInstId").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "procDefId").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "taskInstId").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "taskDefId").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "taskName").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "antecessors").read[List[ParentContext]] and
    (__ \ "internalState").read[InterState] and
    (__ \ "prevStateInstIds").read[List[String]] and
    (__ \ "isTerminate").read[Boolean] and
    (__ \ "nodetype").read[Int])(StateContext.apply _)

  implicit val ContextDataReads: Reads[ContextData] = (
    (__ \ "procPIO").read[Int] and
    (__ \ "taskPIO").read[Int] and
    (__ \ "rolemark").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "startMS").readNullable[Long].map({ _.getOrElse(System.currentTimeMillis()) }) and
    (__ \ "idata1").read[Option[Int]] and
    (__ \ "idata2").read[Option[Int]] and
    (__ \ "strdata1").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "strdata2").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "fdata1").read[Option[Float]] and
    (__ \ "fdata2").read[Option[Float]] and
    (__ \ "taskcenter").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "rootproc").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "extra").read[HashMap[String, Any]])(ContextData.apply _)

  implicit val SubmitStatesReads: Reads[SubmitStates] = (
    (__ \ "state").read[StateContext] and
    (__ \ "submitter").readNullable[String].map({ _.getOrElse(null) }) and
    (__ \ "ctxData").read[ContextData])(SubmitStates.apply _)

}
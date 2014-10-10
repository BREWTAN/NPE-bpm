package controllers

import java.io.FileInputStream
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.Range
import org.nights.npe.po.Definition.CallActivity
import org.nights.npe.po.Definition.Process
import org.nights.npe.utils.POHelper
import org.nights.npe.utils.ProcDefMap
import com.fasterxml.jackson.databind.JsonNode
import com.github.mauricio.async.db.QueryResult
import akka.actor.actorRef2Scala
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka
import org.nights.npe.backend.db.KOProcdef
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.DeserializationFeature
import org.nights.npe.backend.db.TasksDAO
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.KOTasks

object ProcInstFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  def test = Action {
    Ok("kkk");
  }
  def getOrNone(value: Any): String = {
    if (value == null) null
    else
      value.asInstanceOf[String]
  }

  def getByPage(skip: Int, limit: Int, status: Int, query: String) = Action.async { request =>

    println("query==" + query)
    var qq="";

    if(query!=null&&query.length()>0){
        val mapper = new ObjectMapper()
        mapper.registerModule(DefaultScalaModule)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        val querytask = mapper.readValue(query, classOf[KOTasks])
       if(querytask.procdefid!=null&&querytask.procdefid.length()>0)qq+=" and procdefid like '%"+querytask.procdefid+"%'"
       
       if(querytask.procinstid!=null&&querytask.procinstid.length()>0)qq+=" and procinstid like '%"+querytask.procinstid+"%'"
       
       querytask.interstate match{
         case Some(v) if v>=0 => qq+=" and interstate = "+v
         case Some(v) if v == (-2) => qq+=" and interstate not in ( 0,1,2,3,8)" 

         case _ =>
       }
    }
    println("query.qq="+qq)
    val results = for {
      total <- TasksDAO.countByCond("taskdefid='_1'"+qq)
      rows <- TasksDAO.findByCond("taskdefid='_1'"+qq, Range(skip, limit))
    } yield (total, rows)

    results.map({ f =>
      val jsonRet = Json.obj("total_rows" -> f._1.get,
        "count" -> f._2.rowsAffected,
        "fromRow" -> skip,
        "limit" -> limit,
        "rows" -> f._2.rows.map({ rs =>
          rs.map({ row =>
            Json.obj("procinstid" -> row("procinstid").asInstanceOf[String],
              "procdefid" -> row("procdefid").asInstanceOf[String],
              "status" -> row("interstate").asInstanceOf[Int],
              "createtime" -> row("createtime").asInstanceOf[String],
              "termtime" -> getOrNone(row("submittime")),
              "procPIO" -> row("procPIO").asInstanceOf[Int],
              "idata1" -> row("idata1").asInstanceOf[Int],
              "idata2" -> row("idata2").asInstanceOf[Int],
              "strdata1" -> row("strdata1").asInstanceOf[String],
              "strdata2" -> row("strdata2").asInstanceOf[String],
              "fdata1" -> row("fdata1").asInstanceOf[Float],
              "fdata2" -> row("fdata2").asInstanceOf[Float],
              "jsondata" -> row("jsondata").asInstanceOf[String],
              "submitter" -> row("submitter").asInstanceOf[String])

          })
        }))
      Ok(jsonRet)

    })
  }
  

  def delete(defid: String) = Action.async { request =>
    val cond = KOTasks(null,null , defid, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    TasksDAO.deleteByCond(cond).map({ f =>
      println("okok:rowsAffected="+f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

}
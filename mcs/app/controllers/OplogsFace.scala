package controllers

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.backend.db.KOTaskRole
import org.nights.npe.backend.db.KOTaskRole
import org.nights.npe.backend.db.Range
import org.nights.npe.backend.db.TaskRoleDAO
import org.nights.npe.po.Definition.Process
import org.nights.npe.po.Definition.UserTask
import org.nights.npe.utils.ProcDefMap
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.mauricio.async.db.QueryResult
import akka.actor.actorRef2Scala
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka
import scala.collection.mutable.HashMap
import com.github.mauricio.async.db.mysql.exceptions.MySQLException
import org.nights.npe.mcs.db.DBOplogs._
import java.util.UUID

object OplogsFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  val DAO=OplogsDAO;
  val KO=KOOplogs
  
  def getOrNone(value: Any): String = {
    if (value == null) null
    else
      value.asInstanceOf[String]
  }

  def getByPage(skip: Int, limit: Int, status: Int, query: String, page: Boolean) = Action.async { request =>

    println("query==" + query)
    var qq = "";

    if (query != null && query.length() > 0) {
      val mapper = new ObjectMapper()
      mapper.registerModule(DefaultScalaModule)
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      val queryBean = mapper.readValue(query, classOf[KOOplogs])
      if (queryBean.searchkey  != null && queryBean.searchkey.length() > 0) qq += " and searchkey like '%" + queryBean.searchkey + "%'"
      if (queryBean.message   != null && queryBean.message.length() > 0) qq += " and message like '%" + queryBean.message + "%'"

      if (queryBean.uuid != null && queryBean.uuid.length() > 0) qq += " and uuid = '" + queryBean.uuid + "'"
      if (queryBean.level != null && queryBean.level.length() > 0) qq += " and level = '" + queryBean.uuid + "'"

    }
    println("query.qq=" + qq)
    if (page) {
	    val results = for {
	      total <- DAO.countByCond("1=1" + qq)
	      rows <- DAO.findByCond("1=1" + qq +" order by createtime desc", Range(skip, limit))
	    } yield (total, rows)
      results.map({ f =>
        val jsonRet = Json.obj("total_rows" -> f._1.get,
          "count" -> f._2.rowsAffected,
          "fromRow" -> skip,
          "limit" -> limit,
          "rows" -> f._2.rows.map({ rs =>
            rs.map({ row =>
              Json.obj("uuid" -> row("uuid").asInstanceOf[String],
                "searchkey" -> row("searchkey").asInstanceOf[String],
                "message" -> row("message").asInstanceOf[String],
                "level" -> row("level").asInstanceOf[String],
                "createtime" -> row("createtime").asInstanceOf[String].toLong)})
          }))
        Ok(jsonRet)

      })
    } else {
      DAO.findByCond("1=1" + qq +" order by createtime desc", Range(skip, limit)).map({ f =>
        val jsonRet = Json.toJson(f.rows.map({ rs =>
          rs.map({ row =>
            Json.obj("uuid" -> row("uuid").asInstanceOf[String],
                "searchkey" -> row("searchkey").asInstanceOf[String],
                "message" -> row("message").asInstanceOf[String],
                "level" -> row("level").asInstanceOf[String],
                "createtime" -> row("createtime").asInstanceOf[String].toLong)
          })
        }))
        Ok(jsonRet)
      })
    }
  }

  def delete(keyy: String) = Action.async { request =>
    DAO.delete(keyy).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def insert() = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KO((jsonStr \ "searchkey").as[String], (jsonStr \ "message").as[String], (jsonStr \ "jsonStr").as[String])
    println("insert:" + bean)
    DAO.insertOrUpdate(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def update(keyy: String) = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KO((jsonStr \ "searchkey").as[String], (jsonStr \ "message").as[String], (jsonStr \ "jsonStr").as[String],
         (jsonStr \ "uuid").as[String])
    println("insert:" + bean)
    DAO.updateSelective(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

 
}
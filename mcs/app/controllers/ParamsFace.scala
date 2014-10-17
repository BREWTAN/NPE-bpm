package controllers

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import org.nights.npe.backend.db.Range
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
import org.nights.npe.backend.db.KOParams
import org.nights.npe.backend.db.ParamsDAO

object ParamsFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  val DAO=ParamsDAO;
  val KO=KOParams
  
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
      val queryBean = mapper.readValue(query, classOf[KOParams])
      if (queryBean.keyy   != null && queryBean.keyy.length() > 0) qq += " and keyy like '%" + queryBean.keyy + "%'"

    }
    println("query.qq=" + qq)
    if (page) {
	    val results = for {
	      total <- DAO.countByCond("1=1" + qq)
	      rows <- DAO.findByCond("1=1" + qq , Range(skip, limit))
	    } yield (total, rows)
      results.map({ f =>
        val jsonRet = Json.obj("total_rows" -> f._1.get,
          "count" -> f._2.rowsAffected,
          "fromRow" -> skip,
          "limit" -> limit,
          "rows" -> f._2.rows.map({ rs =>
            rs.map({ row =>
              Json.obj("keyy" -> row("keyy").asInstanceOf[String],
                "valuee" -> row("valuee").asInstanceOf[String],
                "des" -> row("des").asInstanceOf[String])})
          }))
        Ok(jsonRet)

      })
    } else {
      DAO.findByCond("1=1" + qq, Range(skip, limit)).map({ f =>
        val jsonRet = Json.toJson(f.rows.map({ rs =>
          rs.map({ row =>
            Json.obj("keyy" -> row("keyy").asInstanceOf[String],
                "valuee" -> row("valuee").asInstanceOf[String],
                "des" -> row("des").asInstanceOf[String])
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
    val bean = KO((jsonStr \ "keyy").as[String], (jsonStr \ "valuee").as[String], (jsonStr \ "des").as[String])
    println("insert:" + bean)
    DAO.insertOrUpdate(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def update(keyy: String) = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KO((jsonStr \ "keyy").as[String], (jsonStr \ "valuee").as[String], (jsonStr \ "des").as[String])
    println("insert:" + bean)
    DAO.updateSelective(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

 
}
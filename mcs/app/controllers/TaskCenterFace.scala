package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.nights.npe.backend.db.KOTaskCenter
import org.nights.npe.backend.db.Range
import org.nights.npe.backend.db.TaskCenterDAO
import org.nights.npe.utils.ProcDefMap

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.mauricio.async.db.QueryResult
import org.nights.npe.po.Definition.Process

import akka.actor.actorRef2Scala
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Result
import play.libs.Akka

object TaskCenterFace extends Controller {

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  def test = Action {
    Ok("kkk");
  }
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
      val queryBean = mapper.readValue(query, classOf[KOTaskCenter])
      if (queryBean.centerid != null && queryBean.centerid.length() > 0) qq += " and centerid like '%" + queryBean.centerid + "%'"

      if (queryBean.centername != null && queryBean.centername.length() > 0) qq += " and centername like '%" + queryBean.centername + "%'"

      queryBean.status match {
        case Some(v) if v >= 0 => qq += " and status = " + v
        case Some(v) if v == (-2) => qq += " and status not in ( 0,1,2,3,8)"
        case _ =>
      }
    }
    println("query.qq=" + qq)
    val results = for {
      total <- TaskCenterDAO.countByCond("1=1" + qq)
      rows <- TaskCenterDAO.findByCond("1=1" + qq, Range(skip, limit))
    } yield (total, rows)
    if (page) {

      results.map({ f =>
        val jsonRet = Json.obj("total_rows" -> f._1.get,
          "count" -> f._2.rowsAffected,
          "fromRow" -> skip,
          "limit" -> limit,
          "rows" -> f._2.rows.map({ rs =>
            rs.map({ row =>
              Json.obj("centerid" -> row("centerid").asInstanceOf[String],
                "centername" -> row("centername").asInstanceOf[String],
                "status" -> row("status").asInstanceOf[Int],
                "createtime" -> row("createtime").asInstanceOf[String],
                "modtime" -> row("modtime").asInstanceOf[String])
            })
          }))
        Ok(jsonRet)

      })
    } else {
      results.map({ f =>
        val jsonRet = Json.toJson(f._2.rows.map({ rs =>
            rs.map({ row =>
              Json.obj("centerid" -> row("centerid").asInstanceOf[String],
                "centername" -> row("centername").asInstanceOf[String],
                "status" -> row("status").asInstanceOf[Int],
                "createtime" -> row("createtime").asInstanceOf[String],
                "modtime" -> row("modtime").asInstanceOf[String])
            })
          }))
        Ok(jsonRet)

      })
    }
  }

  def delete(keyy: String) = Action.async { request =>
    TaskCenterDAO.delete(keyy).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def insert() = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KOTaskCenter((jsonStr \ "centerid").as[String], (jsonStr \ "centername").as[String], (jsonStr \ "status").as[Option[Int]])
    println("insert:" + bean)
    TaskCenterDAO.insertOrUpdate(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def update(keyy: String) = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KOTaskCenter((jsonStr \ "centerid").as[String], (jsonStr \ "centername").as[String], (jsonStr \ "status").as[Option[Int]], null,
      Some(System.currentTimeMillis()))
    println("insert:" + bean)
    TaskCenterDAO.updateSelective(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })

  }
}
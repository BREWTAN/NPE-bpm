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

object TaskRoleFace extends Controller {

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
      val queryBean = mapper.readValue(query, classOf[KOTaskRole])
      if (queryBean.roleid != null && queryBean.roleid.length() > 0) qq += " and roleid like '%" + queryBean.roleid + "%'"

      if (queryBean.rolename != null && queryBean.rolename.length() > 0) qq += " and rolename like '%" + queryBean.rolename + "%'"

      if (queryBean.centerid != null && queryBean.centerid.length() > 0) qq += " and centerid like '%" + queryBean.centerid + "%'"

      queryBean.status match {
        case Some(v) if v >= 0 => qq += " and status = " + v
        case Some(v) if v == (-2) => qq += " and status not in ( 0,1,2,3,8)"
        case _ =>
      }
    }
    println("query.qq=" + qq)
    val results = for {
      total <- TaskRoleDAO.countByCond("1=1" + qq)
      rows <- TaskRoleDAO.findByCond("1=1" + qq, Range(skip, limit))
    } yield (total, rows)
    if (page) {
      results.map({ f =>
        val jsonRet = Json.obj("total_rows" -> f._1.get,
          "count" -> f._2.rowsAffected,
          "fromRow" -> skip,
          "limit" -> limit,
          "rows" -> f._2.rows.map({ rs =>
            rs.map({ row =>
              Json.obj("roleid" -> row("roleid").asInstanceOf[String],
                "rolename" -> row("rolename").asInstanceOf[String],
                "centerid" -> row("centerid").asInstanceOf[String],

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
            Json.obj("roleid" -> row("roleid").asInstanceOf[String],
              "rolename" -> row("rolename").asInstanceOf[String],
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
    TaskRoleDAO.delete(keyy).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def insert() = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KOTaskRole((jsonStr \ "roleid").as[String], (jsonStr \ "centerid").as[String], (jsonStr \ "rolename").as[String], (jsonStr \ "status").as[Option[Int]])
    println("insert:" + bean)
    TaskRoleDAO.insertOrUpdate(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def update(keyy: String) = Action.async { request =>
    val jsonStr = request.body.asJson.get
    val bean = KOTaskRole((jsonStr \ "roleid").as[String], (jsonStr \ "centerid").as[String], (jsonStr \ "rolename").as[String], (jsonStr \ "status").as[Option[Int]], null,
      Some(System.currentTimeMillis()))
    println("insert:" + bean)
    TaskRoleDAO.updateSelective(bean).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }

  def importFromFlows() = Action.async { request =>
    val result = ProcDefDAO.findAll()
    val validator = new ProcDefMap
    result.map { qr =>
      if (qr.rowsAffected > 0) {
        ProcDefDAO.resultRow(qr).asInstanceOf[List[KOProcdef]].map { ko =>
          validator.appendDefXML(ko.xmlbody)
        }
        if (!validator.checkIntegrity) {
          Ok(Json.obj("status" -> "-1",
            "message" -> "流程校验失败"))
        } else {
          //          println(",," + validator.procDefs)
          val vmap: HashMap[String, KOTaskRole] = new HashMap;
          validator.procDefs.map({ kv =>
            val v = kv._2
            val p: Process = v.asInstanceOf[Process]
            println("Process node:" + p)
            p.nodes.values.foreach({ node =>
              node match {
                case task: UserTask =>
                  vmap.+=((task.taskName, KOTaskRole(task.taskName, null, "FF" + p.e.name + "_" + task.taskName,Some(1))))
                case a @ _ =>
                  println("unkonw node:" + a)
              }
            })
          })
          println("insert:" + vmap)
          if (vmap.size > 0) {
            try {
              val result = Await.result(TaskRoleDAO.insertBatch(vmap.values.toList), 60 seconds)
              Ok(Json.obj("status" -> result.rowsAffected))
            } catch {
              case sqlerr: MySQLException if sqlerr.errorMessage.errorCode == 1062 => {
                var cc = 0;
                implicit val noexec: Boolean = true;

                val dolist = vmap.values.map({ f =>
                  TaskRoleDAO.insertOrUpdate(f)
                })
                val resultl = Await.ready(Future.sequence(dolist.toList), 10 seconds)
                println("resultready==" + result.value.get.get)
                try {
                  val batch = TaskRoleDAO.execInBatch(resultl.value.get.get)
                  val result = Await.result(batch, 60 seconds)
                  Ok(Json.obj("status" -> dolist.size))
                } catch {
                  case sqlerr: MySQLException => Ok(Json.obj("status" -> -2, "message" -> sqlerr.errorMessage.errorMessage, "sqlcode" -> sqlerr.errorMessage.errorCode))
                }
              }

              case sqlerr: MySQLException => {
                Ok(Json.obj("status" -> -2, "message" -> sqlerr.errorMessage.errorMessage, "sqlcode" -> sqlerr.errorMessage.errorCode))
              }
            }

          } else {
            Ok(Json.obj("status" -> 0))
          }

        }
      } else {
        Ok(Json.obj("status" -> "-1", "message" -> "没找到任何流程"))
      }
    }
  }
}
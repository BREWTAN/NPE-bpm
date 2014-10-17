package controllers

import scala.concurrent.ExecutionContext
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.Range
import org.nights.npe.backend.db.TasksDAO
import org.nights.npe.mcs.akka.Global
import org.nights.npe.mo.Transition
import org.nights.npe.utils.BeanTransHelper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import akka.actor.ActorSelection.toScala
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import play.libs.Akka
import org.nights.npe.po.StateContextWithData
import scala.concurrent.Await
import akka.pattern.ask
import org.nights.npe.mo.RecycleTasks
import scala.concurrent.duration.DurationInt
import com.github.mauricio.async.db.QueryResult
import scala.concurrent.Future
import scala.util.Success

case class KOTaskQuery(val taskdefid: String = null, val taskinstid: String = null, val interstate: Option[Int] = null, val taskname: String = null)

object TaskInstFace extends Controller {

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
      val querytask = mapper.readValue(query, classOf[KOTaskQuery])
      if (querytask.taskdefid != null && querytask.taskdefid.length() > 0) qq += " and taskdefid like '%" + querytask.taskdefid + "%'"
      if (querytask.taskname != null && querytask.taskname.length() > 0) qq += " and taskname like '%" + querytask.taskname + "%'"

      if (querytask.taskinstid != null && querytask.taskinstid.length() > 0) qq += " and taskinstid like '%" + querytask.taskinstid + "%'"

      querytask.interstate match {
        case Some(v) if v >= 0 => qq += " and interstate = " + v
        case Some(v) if v == (-2) => qq += " and interstate not in ( 0,1,2,3,8)"

        case _ =>
      }
    }
    println("query.qq=" + qq)
    val results = for {
      total <- TasksDAO.countByCond("nodetype=0" + qq)
      rows <- TasksDAO.findByCond("nodetype=0" + qq, Range(skip, limit))
    } yield (total, rows)

    results.map({ f =>
      val jsonRet = Json.obj("total_rows" -> f._1.get,
        "count" -> f._2.rowsAffected,
        "fromRow" -> skip,
        "limit" -> limit,
        "rows" -> f._2.rows.map({ rs =>
          rs.map({ row =>
            Json.obj("taskinstid" -> row("taskinstid").asInstanceOf[String],
              "taskdefid" -> row("taskdefid").asInstanceOf[String],
              "status" -> row("interstate").asInstanceOf[Int],
              "createtime" -> row("createtime").asInstanceOf[String],
              "termtime" -> getOrNone(row("submittime")),
              "procPIO" -> row("procPIO").asInstanceOf[Int],
              "taskPIO" -> row("taskPIO").asInstanceOf[Int],
              "taskname" -> row("taskname").asInstanceOf[String],
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
    val cond = KOTasks(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, defid, null);
    TasksDAO.deleteByCond(cond).map({ f =>
      println("okok:rowsAffected=" + f.rowsAffected)
      Ok(Json.obj("rowsAffected" -> f.rowsAffected))
    })
  }
  lazy val poller = Global.system.actorSelection("/user/poll");

  def recycle(defids: String) = Action.async { request =>
    val ff = TasksDAO.findByCond("taskinstid in (" + defids + ")", null);
    val rr = Await.ready(ff, 10 seconds)
    println("rr.value=="+rr.value.get.getClass())
    rr.value.get match {
      case Success(f: QueryResult) =>
        TasksDAO.resultRow(f) match {
          case a: List[KOTasks] =>
            val tasks = a.map(row => {
              val ko = row.asInstanceOf[KOTasks]
              val rel = BeanTransHelper.koToState(ko);
              StateContextWithData(rel._1, rel._2)
            })
            poller.ask(RecycleTasks(tasks))(10 seconds).map({ result =>
              if (result == "recycle.ok") {
                Ok(Json.obj("rowsAffected" -> f.rowsAffected))
              } else {
                Ok(Json.obj("rowsAffected" -> 0, "statusMessage" -> ("" + result)))
              }
            });
          case _ =>
            Future { Ok(Json.obj("rowsAffected" -> 1, "statusMessage" -> ("" + f.statusMessage))) }
        }
      case _ =>
        Future { Ok(Json.obj("rowsAffected" -> -1, "statusMessage" -> "db error")) }

    }

  }

}
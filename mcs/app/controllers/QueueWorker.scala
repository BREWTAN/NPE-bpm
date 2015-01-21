package controllers

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.mvc.Action
import play.api.mvc.Controller
import play.libs.Akka
import akka.pattern.ask
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import scala.concurrent.Await
import org.nights.npe.mcs.akka.Global
import java.util.concurrent.atomic.AtomicLong
import org.nights.npe.mcs.akka.QueryTasks
import scala.concurrent.duration.DurationInt
import org.nights.npe.mcs.akka.PollWorker
import com.fasterxml.jackson.databind.DeserializationFeature
import org.nights.npe.mo.SubmitStates
import scala.concurrent.Future
import org.nights.npe.po.StateContext
import org.nights.npe.po.ParentContext
import play.api.libs.json.Json
import org.nights.npe.po.DoneStateContext
import org.nights.npe.mo.TaskDone
import org.nights.npe.mo.ANewProcess
import org.nights.npe.po.ContextData
import java.util.UUID
import play.api.libs.json.Reads
import scala.collection.mutable.HashMap
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Format
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import org.nights.npe.mcs.db.DBOplogs._
import akka.pattern.ask
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.DurationInt
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.po.AskResult
import org.nights.npe.mcs.db.DBOplogs
import org.nights.npe.mo.NoneStateInQueue
import org.nights.npe.mcs.akka.StatsCounter
import org.nights.npe.mo.ChangeTaskState
import org.nights.npe.po.InterState
import org.nights.npe.po.InterStateHangup
import org.nights.npe.backend.db.TasksDAO
import org.nights.npe.po.InterStateHangup
import org.nights.npe.backend.db.Range
import scala.util.Success
import com.github.mauricio.async.db.QueryResult
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.utils.BeanTransHelper
import org.nights.npe.mo.ObtainedStates
import org.nights.npe.mo.Obtainer
import com.github.mauricio.async.db.QueryResult
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.ObtainTasksDAO
import org.nights.npe.backend.db.KOObtainTasks
import org.nights.npe.mo.ChangeQueuePIO
object QueueWorker
  extends Controller {
  import org.nights.npe.mcs.akka.JerksonHelper._

  implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("simple-db-lookups")

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  lazy val poller = Global.system.actorSelection("/user/poll");
  val counter = new AtomicLong(0)
  def nullcheck(vl: String): String = {
    vl match {
      case str if (str == null || str.length() == 0 || str.equals("undefined")) => null
      case _ => vl
    }
  }
  def obtainByRole(obtainerjj: String, rolejj: String, centerjj: String) = Action.async { request =>
    val obtainer = nullcheck(obtainerjj)
    val role = nullcheck(rolejj);
    val center = nullcheck(centerjj);

    poller.ask(QueryTasks(1, obtainer, role, center, obtainer))(30 seconds).map(result => {
      val jsonResult = mapper.writeValueAsString(result)

      if (result.isInstanceOf[ObtainedStates]) {
        val o = result.asInstanceOf[ObtainedStates]
        if (o != null && o.state != null) {
          Await.ready(OplogsDAO.insert(KOOplogs(obtainer + " 获取任务 " + o.state.taskName, o.state.taskInstId + "," + obtainer)), 10 seconds)

          StatsCounter.obtains.incrementAndGet()

        }

      }
      Ok(jsonResult)
    }) recover {
      case e: akka.pattern.AskTimeoutException => Ok("{}")
    }
  }
  def obtainFromHangup(obtainer: String, role: String, center: String, states: String,extendsql:String) = Action.async { request =>

    println("obtainFromHangupFirst," + obtainer + ",state===" + states)
    if (states == null || states.equals("undefined")) {
      Future { 1 } map { r =>
        Ok("{\"status\":-1,\"msg\":\" states is null :" + states + "\"}")
      }
    } else {
      val cond = "interstate in (" + states + ")" + {
        obtainer match {
          case str: String if (str != null && str.length() > 0) => " and obtainer='" + str + "' "
          case _ => ""
        }
      } + {
        role match {
          case str: String if (str != null && str.length() > 0) => " and taskname='" + str + "' "
          case _ => ""
        }
      }+ {
        center match {
          case str: String if (str != null && str.length() > 0) => " and taskcenter='" + str + "' "
          case _ => ""
        }
      } + {
        extendsql match {
          case str: String if (str != null && str.length() > 0) => " and "+extendsql+" "
          case _ => ""
        }
      } + " order by obtaintime asc"

      val noneResult = mapper.writeValueAsString(ObtainedStates(
        null, null, Obtainer(obtainer)))

      synchronized {

        val result = TasksDAO.findByCond(cond, Range(0, 1))
        Await.ready(result, 10 seconds).value.getOrElse(None) match {
          case Success(qr: QueryResult) =>
            if (qr.rowsAffected > 0) {
              val ko = TasksDAO.resultRow(qr).asInstanceOf[List[KOTasks]].head
              val sd = BeanTransHelper.koToState(ko)
              val jsonResult = mapper.writeValueAsString(ObtainedStates(
                sd._1, sd._2, Obtainer(obtainer, role, center)))
              ObtainTasksDAO.update(KOObtainTasks(sd._1.taskInstId, obtainer)).map({ qr =>
                Ok(jsonResult)
              })
            } else {
              Future { 1 } map { r =>
                Ok(noneResult)
              }
            }
          case a @ _ =>
            println("getResult:" + a)

            Future { 1 } map { r =>
              Ok(noneResult)
            }
        }
      }
    }

  }
  def newProc(submiter: String, center: String, procdef: String,procinstid :String ) = Action.async { request =>
    val jsonbody = request.body.asJson.get

    val ss = Json.fromJson[ContextData](jsonbody)
    if (ss.isSuccess) {
      //ANewProcess(procInstId: String, submitter: String, procDefId: String, data: ContextData)
      println("success==" + ss)
      
      val procid = procinstid match {
        case str if (str!=null) => procinstid
        case _ => UUID.randomUUID().toString()
      }
      submitor.ask(ANewProcess(procid, submiter, procdef, ss.get))(10 seconds) map (result => {
        val jsonResult = mapper.writeValueAsString(result)
        Await.ready(OplogsDAO.insert(DBOplogs.KOOplogs(submiter + " 新建流程 " + procdef, procid + "," + submiter + "," + procdef)), 10 seconds)
        StatsCounter.newprocs.incrementAndGet()
        Ok(jsonResult)
      })
    } else {
      Future { 1 }.map({ f =>
        println("error==" + ss)
        Ok("{\"status\":-1,\"msg\":\"" + mapper.writeValueAsString(ss) + "\"}")
      })
    }
  }

  lazy val submitor = Global.system.actorSelection("/user/submitor");

  def submit = Action { request =>
    val jsonbody = request.body.asJson.get
    println("jsonboy==" + jsonbody)

    val ss = Json.fromJson[SubmitStates](jsonbody)

    if (ss.isSuccess) {
      println("success==" + ss)
      val sub = ss.get

      Await.ready(OplogsDAO.insert(DBOplogs.KOOplogs(sub.submitter + " 提交任务 " + sub.state.taskName, sub.state.taskInstId + "," + sub.submitter)), 10 seconds)

      submitor ! DoneStateContext(sub.state, sub.ctxData, sub.submitter)
      StatsCounter.submits.incrementAndGet()
      Ok("{\"status\":0}")
    } else {
      println("error==" + ss)
      Ok("{\"status\":-1,\"msg\":\"" + ss + "\"}")
    }
  }

  def changePIO(taskId:String,newPIO:Int)  = Action.async { request =>
    poller.ask(ChangeQueuePIO(taskId,newPIO))(60 seconds) map ( result =>{
      result match{
        case Some(id) => Ok("{\"status\": " + 1 + ",\"msg\":\"" + id + "\"}")
        case _ => Ok("{\"status\": " + 0 + "\"}")
      }
    }
    )
  }
  def hangup(operator: String, taskinstid: String) = Action.async { request =>
    submitor.ask(ChangeTaskState(taskinstid, InterStateHangup().v))(10 seconds) map (result => {
      val msg =
        result match {
          case "error" => ("error", -1)
          case _ => ("成功", 0)
        }
      val jsonResult = mapper.writeValueAsString(result)
      Await.ready(OplogsDAO.insert(DBOplogs.KOOplogs(operator + " " + msg._1 + "挂起任务 " + taskinstid, operator + " 挂起任务 " + taskinstid + "::" + result.toString)), 10 seconds)
      Ok("{\"status\": " + msg._2 + ",\"msg\":\"" + msg._1 + "\"}")
    })

  }
}
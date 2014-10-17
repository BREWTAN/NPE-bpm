package org.nights.npe.fsm.backend

import java.io.File
import org.nights.npe.fsm.ActorHelper
import org.nights.npe.utils.ProcDefHelper
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import org.nights.npe.utils.ProcDefHelper
import org.nights.npe.backend.db.KOProcdef
import org.nights.npe.backend.db.ProcDefDAO
import org.nights.npe.utils.ProcDefMap
import com.github.mauricio.async.db.exceptions.DatabaseException
import com.github.mauricio.async.db.QueryResult
import org.nights.npe.backend.db.DBResult
import org.nights.npe.backend.db.KOProcdef
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class LoadDefFile(filename: String);
case class LoadDefFromDB();

class ProcDefStorage extends Actor with ActorLogging with ActorHelper {

  override def preStart(): Unit = {
    log.info("startup@{}", self)
    self ! LoadDefFromDB()
  }
  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case LoadDefFile(filename) => {
      ProcDefHelper.appendDefFile(filename);
    }
    case "reloadfromdb" =>{
          self ! LoadDefFromDB()

    }
    case "stop" =>{
        context.system.shutdown

    }
    case LoadDefFromDB() => {
      implicit def f(result: DBResult): Unit = {
        result match {
          case DBResult(exception: DatabaseException) => {
            log.error("failed:" + exception)
          }
          case DBResult(rows: List[Any]) => {
            if (rows.size == 0) println("none result")
            rows.map({ row =>
//              println("row=" + row)
              val ko = row.asInstanceOf[KOProcdef]
              ProcDefHelper.appendDefXML(ko.xmlbody)
            })

            if (!ProcDefHelper.checkIntegrity) {
              log.error("流程定义解析失败!");
              ProcDefHelper.procDefs.filter(!_._2.isvalid).map(keyV => {
                log.error("流程{}:@{}", keyV._1, keyV._2.parseError.mkString(","))
              })
            } else {

              ProcDefHelper.procDefs.map(keyV => {
                log.info("流程{}:@{}", keyV._1, keyV._2.e.name)
                keyV._2.nodes.map({ node =>
                  if (node._2.taskName == null) {
                    log.error("流程{}@{}节点任务为空:", keyV._2.e.name, node)
                  }
                })
              })

              log.info("流程定义解析成功!");
            }
          }
          case DBResult(qr: QueryResult) => {
            log.error("qr=" + qr)
          }
          case a @ _ => log.error("unknow result:" + a)
        }
      }

      val result = ProcDefDAO.findAll()
      Await.ready(result, 60 seconds)

    }
    case _ =>
  }
}
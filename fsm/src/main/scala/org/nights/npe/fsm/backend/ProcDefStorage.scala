package org.nights.npe.fsm.backend

import java.io.File

import org.nights.npe.fsm.ActorHelper
import org.nights.npe.fsm.defs.ProcDefHelper
import org.nights.npe.po.Definition._

import akka.actor.Actor
import akka.actor.ActorLogging

case class LoadDefFile(filename: String);
case class LoadDefDir(dir: String);

class ProcDefStorage extends Actor with ActorLogging with ActorHelper {

  override def preStart(): Unit = {
    log.info("startup@{}", self)
    self ! LoadDefDir("/tmp/flows/ccbexample")
  }
  override def postStop(): Unit = {
    log.info("shutdown:{}", self)
  }

  def receive = {
    case LoadDefFile(filename) => {
      ProcDefHelper.appendDefFile(filename);
    }
    case LoadDefDir(dir) => {
      val dirfile = new File(dir);
      for (file <- dirfile.listFiles()) {
        ProcDefHelper.appendDefFile(file.getAbsolutePath());
      }
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
    case _ =>
  }
}
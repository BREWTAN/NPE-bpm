package org.nights.npe.fsm.backend.db

import scala.collection.mutable.StringBuilder
import scala.concurrent.Future
import scala.reflect.classTag

import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.RowData

object ParamsDAO extends SimpleDAO[KOParams] {
  val ttag = classTag[KOParams];
  val tablename = "params";
  val keyname = "keyy"
}

object ProcDefDAO extends SimpleDAO[KOProcdef] {
  val ttag = classTag[KOProcdef];
  val tablename = "procdef";
  val keyname = "defid"

}

object TasksDAO extends SimpleDAO[KOTasks] {
  val ttag = classTag[KOTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object ObtainTasksDAO extends SimpleDAO[KOObtainTasks] {
  val ttag = classTag[KOObtainTasks];
  val tablename = "tasks_obtain";
  val keyname = "taskinstid"
}

object SubmitTasksDAO extends SimpleDAO[KOSubmitTasks] {
  val ttag = classTag[KOSubmitTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object TermTasksDAO extends SimpleDAO[KOTermTask] {
  val ttag = classTag[KOTermTask];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object TermUpdateTasksDAO extends SimpleDAO[KOTermTask] {
  val ttag = classTag[KOTermTask];
  val tablename = "tasks";
  val keyname = "taskinstid"
}
object UpdateObtainTasksDAO extends SimpleDAO[KOObtainTasks] {
  val ttag = classTag[KOObtainTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object UpdateSubmitTasksDAO extends SimpleDAO[KOSubmitTasks] {
  val ttag = classTag[KOSubmitTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}


object ProcinstsDAO extends SimpleDAO[KOProcinsts] {
  val ttag = classTag[KOProcinsts];
  val tablename = "procinsts";
  val keyname = "procinstid"
}

object ConvergeDAO extends SimpleDAO[KOConvergeCounter] {
  val ttag = classTag[KOConvergeCounter];
  val tablename = "convergecounter";
  val keyname = "keyy"
}



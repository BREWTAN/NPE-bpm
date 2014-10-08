package org.nights.npe.fsm.backend.db

import scala.reflect.classTag

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


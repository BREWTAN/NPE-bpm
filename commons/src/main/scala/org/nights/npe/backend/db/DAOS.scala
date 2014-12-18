package org.nights.npe.backend.db

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


object ProcDefCounterDAO extends SimpleDAO[KOProcdefCounter] {
  val ttag = classTag[KOProcdefCounter];
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
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object ChangePIODAO extends SimpleDAO[KOChangePIOTasks] {
  val ttag = classTag[KOChangePIOTasks];
  val tablename = "tasks";
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


object InTermTasksDAO extends SimpleDAO[KOInTermTask] {
  val ttag = classTag[KOInTermTask];
  val tablename = "tasks_term";
  val keyname = "taskinstid" 
}

object TermUpdateTasksDAO extends SimpleDAO[KOTermTask] {
  val ttag = classTag[KOTermTask];
  val tablename = "tasks";
  val keyname = "taskinstid"
}


object TermProcDAO extends SimpleDAO[KOTermProc] {
  val ttag = classTag[KOTermProc];
  val tablename = "tasks";
  val keyname = "procinstid"
}

object UpdateObtainTasksDAO extends SimpleDAO[KOObtainTasks] {
  val ttag = classTag[KOObtainTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object InObtainTasksDAO extends SimpleDAO[KOObtainTasks] {
  val ttag = classTag[KOObtainTasks];
  val tablename = "tasks_obtain";
  val keyname = "taskinstid" 
}

object UpdateSubmitTasksDAO extends SimpleDAO[KOSubmitTasks] {
  val ttag = classTag[KOSubmitTasks];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object InSubmitTasksDAO extends SimpleDAO[KOSubmitTasks] {
  val ttag = classTag[KOSubmitTasks];
  val tablename = "tasks_submit";
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

object TaskCenterDAO extends SimpleDAO[KOTaskCenter] {
  val ttag = classTag[KOTaskCenter];
  val tablename = "taskcenter";
  val keyname = "centerid"
}


object TaskRoleDAO extends SimpleDAO[KOTaskRole] {
  val ttag = classTag[KOTaskRole];
  val tablename = "taskrole";
  val keyname = "roleid"
}

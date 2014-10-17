package org.nights.npe.mcs.db

import java.util.UUID
import org.nights.npe.backend.db.SimpleDAO
import scala.reflect.classTag

object DBOplogs {

  case class KOOplogs(val searchkey: String, val message: String, val level: String = "info", val uuid: String = UUID.randomUUID().toString(),  val createtime: Option[Long] = Some(System.currentTimeMillis()))

  object OplogsDAO extends SimpleDAO[KOOplogs] {
    val ttag = classTag[KOOplogs];
    val tablename = "oplogs";
    val keyname = "uuid"
  }

}
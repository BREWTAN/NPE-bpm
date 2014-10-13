package fsm

import scala.collection.mutable.MutableList
import scala.reflect.ClassTag
import scala.reflect.classTag
import org.nights.npe.fsm.FsmActorsController
import org.nights.npe.fsm.InlineCmdActor
import com.github.mauricio.async.db.exceptions.DatabaseException
import com.github.mauricio.async.db.mysql.exceptions.MySQLException
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import akka.cluster.Cluster
import com.github.mauricio.async.db.QueryResult
import java.util.concurrent.atomic.AtomicLong
import java.util.UUID
import org.nights.npe.backend.db.TasksDAO
import org.nights.npe.backend.db.SubmitTasksDAO
import org.nights.npe.backend.db.KOTasks
import org.nights.npe.backend.db.KOSubmitTasks
import org.nights.npe.backend.db.DBResult
import org.nights.npe.backend.db.KOParams
import org.nights.npe.backend.db.KO
import org.nights.npe.backend.db.SimpleDAO

case class KOQueueStat(val taskname: String = null, val interstate: Option[Int] = null, val counter: Option[Long] = null)
object QueueStatDAO extends SimpleDAO[KOQueueStat] {
  val ttag = classTag[KOQueueStat];
  val tablename = "tasks";
  val keyname = "taskinstid"
}

object TestMysqlSelect {

  def listProperties[T: ClassTag] = {
    // a field is a Term that is a Var or a Val
    println(classTag[T].runtimeClass);
    val fields = classTag[T].runtimeClass.getDeclaredFields();
    fields.map({
      field =>
        println("FF:" + field.getType() + "," + field.getName())
    })
    val constructor = classTag[T].runtimeClass.getConstructors()(0);
    println("construtor=" + constructor)
    val args = Array[AnyRef]("a", "b")
    println("array==" + args)
    val instance = constructor.newInstance(args: _*)
    println("instance=" + instance)
  }

  def main(args: Array[String]) {
    //    listProperties[KOParams];

    implicit def f(result: DBResult): Unit = {
      result match {
        case DBResult(exception: DatabaseException) => {
          println("failed:" + exception)
        }
        case DBResult(rows: List[Any]) => {
          if (rows.size == 0) println("none result")
          rows.map({ row =>
            println("row=" + row)
          })
        }
        case DBResult(qr: QueryResult) => {
          println("qr=" + qr)
        }
        case a @ _ => println("unknow result:" + a)
      }
    }
    val result = QueueStatDAO.exec("select  taskname, interstate,count(*) as counter from tasks group by taskname,interstate order by taskname,interstate",Seq.empty)

    Thread.sleep(2000)

  }

}
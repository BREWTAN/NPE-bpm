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

object TestMysql {

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
    var systems: MutableList[ActorSystem] = MutableList.empty;
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
    val ko = KOParams("cc", "dd","")
    SubmitTasksDAO.updateByCond(KOSubmitTasks(null,null,null,null, "ss"),
      KO.submitFilter("4bb2da87a202ce829158af3838002", 2))

    Thread.sleep(2000)

    val ll: List[String] = List.empty;
    val value=ll.mkString("__LIST__")
    println("ll:"+ll.mkString("__LIST__"))
    
    "".split("_LIST_").map{v=>
      val spv = v.split("!@!")
        if (spv.size == 3) {
          println("OOKKK"+v+"::"+spv)
        }
        else {
          println("error:::antecessors error::"+v+"::"+spv.size)
        }
    }
    println("list="+value.split("__LIST__").toList)
    //    ParamsDAO.insertOrUpdate(KOParams("cc", "eeeefff"))
    //    TasksDAO.insertOrUpdate(KOTasks("task12", "def111", "procinst12", "procdef1", Some(6)))
    //    TasksDAO.insertOrUpdate(KOTasks("task11", "def111", "procinst22", "procdef1", Some(6)))
    //    TasksDAO.insertOrUpdate(KOTasks("task111", "def111", "procinst32", "procdef1", Some(6)))
    //    TasksDAO.updateSelective(KOTasks("task2", "def333", "procs1", null, Some(9)))
    //

    //    TasksDAO.delete("0")
    //    TasksDAO.deleteByCond(KOTasks(null, "def111", "procinst22"))
    //
    //    //    TasksDAO.findAll
    //
    //    TasksDAO.findByCond(KOTasks(null, "def111", "procinst32l"))

    for (i <- 1 to 0) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i).withFallback(ConfigFactory.load))
      systems += system;

      Cluster(system).registerOnMemberUp {
        val fsm = system.actorOf(akka.actor.Props[FsmActorsController], "fsm");

        val cmd = system.actorOf(akka.actor.Props[InlineCmdActor], "cmd");

        val counter = new AtomicLong(0);
        val start = System.currentTimeMillis();
        for (i <- 1 to 20) {
          new Thread(new Runnable() {
            def run() {
              while (true) {
                TasksDAO.insert(KOTasks(UUID.randomUUID().toString(), "def111", "procinst12", "procdef1", Some(6)))
                Thread.sleep(5)
                counter.incrementAndGet();
              }
            }
          }).start()
        }

        while (true) {
          Thread.sleep(5 * 1000);
          println("stats: pps=" + counter.get() * 1000 / ((System.currentTimeMillis() - start))
            + ",count=" + counter.get() + ",time=" + (System.currentTimeMillis() - start) / 1000)
        }

        //        Thread.sleep(5000)
        //        val paramsdao = system.actorOf(akka.actor.Props[ParamsDAO], "paramsdao");

        //        paramsdao ! ("insert",KOParams("bb","vva33"))

        //        paramsdao ! ("insert",KOParams("cc","ccvalu333e"))
        //        paramsdao ! ("find","cc")
        //        paramsdao ! ("find","")

        //        cmd ! ("find",ParamsDAO, "")
        //        cmd ! ("find",ParamsDAO, "cc")

        //        cmd ! ("find", TasksDAO, "")

        //        ParamsDAO.insertOrUpdate(KOParams("cc", "ee"))(printresult[KOParams])

        Thread.sleep(5000)
      }

    }
    Thread.sleep(1000000);
    systems.foreach({ system =>
      system.shutdown
    })
  }

}
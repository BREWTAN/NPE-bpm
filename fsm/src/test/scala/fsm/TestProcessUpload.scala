package fsm

import java.io.File
import org.nights.npe.fsm.backend.db.KOProcdef
import org.nights.npe.fsm.backend.db.ProcDefCounterDAO
import org.nights.npe.fsm.defs.ProcDefHelper
import org.nights.npe.po.Definition.CallActivity
import com.github.mauricio.async.db.QueryResult
import scala.util.Success
import com.github.mauricio.async.db.exceptions.DatabaseException
import org.nights.npe.fsm.backend.db.DBResult
import org.nights.npe.fsm.backend.db.ProcDefDAO
import scala.util.Failure
import akka.dispatch.OnComplete
import akka.dispatch.OnComplete

object TestProcessUpload {

  def main(args: Array[String]) {
    //    implicit val process: Process = POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/seq1.bpmn")
    //	  	 implicit  val process:Process=POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/diverg.bpmn")
    //	  implicit  val process:Process=POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/conveg.bpmn")

    val dirfile = new File("/tmp/flows/ccbexample");
    for (file <- dirfile.listFiles()) {
      ProcDefHelper.appendDefFile(file.getAbsolutePath());
    }

    val beans = ProcDefHelper.procDefs.map(keyV => {
      val proc = keyV._2
      val procdef = KOProcdef(
        proc.id,
        proc.taskName,
        proc.Version,
        proc.Package,
        proc.xmlBody,
        proc.nodes.filter(_._2.isInstanceOf[CallActivity]).mapValues(_.asInstanceOf[CallActivity].calledElement).values.mkString(","),
        Some(System.currentTimeMillis()));

      //      println("getKO::"+procdef)
      procdef
    })

    // ProcDefDAO.insertBatch(beans.toList)

    println("OKOK")

    import scala.concurrent.ExecutionContext.Implicits.global

    ProcDefDAO.countAll().andThen({ 
      case Success(s) =>
        println(s.getOrElse(None))
    })

    //    for ((k, p) <- ProcDefHelper.procDefs) {
    //      val state = ProcDefHelper startStateOn p;

    //      for (i <- 0 to 0) 
    //        myActor ! (state using FlowContext("[" + i + "]",p.id))
    //    }

    Thread.sleep(10000)
    //    system.shutdown

  }
}
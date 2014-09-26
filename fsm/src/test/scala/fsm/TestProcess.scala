package fsm

import org.nights.npe.fsm.TransitionWorker
import org.nights.npe.fsm.defs.ProcDefHelper
import org.nights.npe.po.Definition._

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.FromConfig

object TestProcess {

  def main(args: Array[String]) {
    //    implicit val process: Process = POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/seq1.bpmn")
    //	  	 implicit  val process:Process=POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/diverg.bpmn")
    //	  implicit  val process:Process=POHelper.fromXMLFile("/Users/brew/NPE/workspace/npe/flows/exmples/conveg.bpmn")

    val customConf = ConfigFactory.parseString("""
	      akka.actor.deployment {
	        /myactor {
	          router = round-robin-pool
	          nr-of-instances = 3
	    	} }
	      """)
    val system = ActorSystem("MySystem", ConfigFactory.load(customConf))
    val myActor = system.actorOf(FromConfig.props(Props[TransitionWorker]), "myactor")

    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/seq1.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/andconveg.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/conveg.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/diverg.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/ordiverg.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/xorconveg.bpmn");
    ProcDefHelper.appendDefFile("/Users/brew/NPE/workspace/npe/flows/exmples/xordiverg.bpmn");

//    for ((k, p) <- ProcDefHelper.procDefs) {
//      val state = ProcDefHelper startStateOn p;

//      for (i <- 0 to 0) 
//        myActor ! (state using FlowContext("[" + i + "]",p.id))
//    }
    
    Thread.sleep(10000)
//    system.shutdown

  }
}
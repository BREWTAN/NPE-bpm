import scala.xml.NodeSeq
import scala.xml.XML

import org.nights.npe.utils.POHelper

object TestXML {

   def imtest( a:String)(implicit b:String, aa:String, c:Map[String,String])= {
    println(a+","+b+","+c+",")
  }
  def main(args: Array[String]) {
	  val x=XML.loadFile("/Users/brew/NPE/processdesign/prod/process_1.bpmn");
	  val process=POHelper.fromXML(x);
	  
	  println("process="+process);
	  
  }

   
  def checkexist(implicit name:NodeSeq ):Boolean = {
    
    return false;
  }
}
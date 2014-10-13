package org.nights.npe.queue

import java.util.concurrent.locks.ReentrantLock

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import org.nights.npe.fsm.PriorityAware
import scala.util.control.Breaks._

class RolePIOQueue[T <: PriorityAware] {
  val queues: Map[String, AdvancePriorityQueue[T]] = Map.empty
  val queuesList: ListBuffer[AdvancePriorityQueue[T]] = ListBuffer.empty
    val queuesPIOList: ListBuffer[Int] = ListBuffer.empty

  val addLock = new ReentrantLock()

  override def toString:String={
//    queues.mkString("Q", ",", "]")
    queuesList.mkString("[",",","]")
  }
  def insertNewRoleQueue(sc: T,lq: AdvancePriorityQueue[T]): Unit =synchronized{
    var index=queuesPIOList.size;
          breakable{
	          for (i <- 0 to queuesPIOList.size-1) {
	            if(sc.pio>=queuesPIOList(i))
	            {
	               index=i
	                break
	            }
	          }
          }
          queuesList.insert(index,lq)
	      queuesPIOList.insert(index,sc.pio)
          queues.+=((sc.role, lq))
  }
  def offer(sc: T)(implicit lq: AdvancePriorityQueue[T] = null): Unit = {
    queues.get(sc.role) match {
      case Some(q) => q.offer(sc)
      case None => {
        if (lq != null) {
          lq.offer(sc)
          insertNewRoleQueue(sc,lq)
        } else {
          addLock.lock()
          offer(sc)(new AdvancePriorityQueue(sc.role))
          addLock.unlock();
        }
      }
    }
  }

  def poll(role: String = null,filter: String = null): Option[T] = {
    if (role == null) {
      for (q <- queuesList) {
        q.poll(filter) match {
          case some@Some(task) => return some
          case _ => 
        }
      }
      return None
    } else {
      queues.get(role) match {
        case Some(q) => return q.poll(filter)
        case _ => return None
      }
    }
  }

}
package org.nights.npe.queue

import java.util.concurrent.locks.ReentrantLock

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import org.nights.npe.fsm.PriorityAware

class RolePIOQueue[T <: PriorityAware] {
  val queues: Map[String, AdvancePriorityQueue[T]] = Map.empty
  val queuesList: ListBuffer[AdvancePriorityQueue[T]] = ListBuffer.empty
  val addLock = new ReentrantLock()

  override def toString:String={
    queues.mkString("Q", ",", "]")
//    queuesList.mkString("RolePIOQueue[",",","]")
  }
  def offer(sc: T)(implicit lq: AdvancePriorityQueue[T] = null): Unit = {
    queues.get(sc.role) match {
      case Some(q) => q.offer(sc)
      case None => {
        if (lq != null) {
          lq.offer(sc)
          queuesList.+=(lq)
          queues.+=((sc.role, lq))
        } else {
          addLock.lock()
          offer(sc)(new AdvancePriorityQueue)
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
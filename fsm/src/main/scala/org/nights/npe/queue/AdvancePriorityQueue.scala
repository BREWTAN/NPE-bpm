package org.nights.npe.queue

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import org.nights.npe.po.PriorityAware
import org.nights.npe.po.StateContextWithData

class PIOQueue[T](val pio: Int = 0, val __link: LinkedBlockingQueue[T] = new LinkedBlockingQueue[T]) {
  val offerCC: AtomicInteger = new AtomicInteger(0)
  val obtainCC: AtomicInteger = new AtomicInteger(0)

  def offer(sc: T)(implicit reput: Boolean = false) {
    __link.offer(sc)
    if (reput) {
      offerCC.decrementAndGet()
      obtainCC.decrementAndGet()
    } else {
      offerCC.incrementAndGet()
    }
  }
  def poll(): T = {
    val ret = __link.poll()
    if (ret != null) obtainCC.incrementAndGet();

    ret
  }
  def checkIfExist(sc: T): Boolean = {
    __link.toArray[StateContextWithData](Array.empty).filter(_.asInstanceOf[StateContextWithData].sc.taskInstId.equals(sc.asInstanceOf[StateContextWithData].sc.taskInstId))
      .size > 0
  }
  def size(): Int = __link.size()
}
class AdvancePriorityQueue[T <: PriorityAware](role: String) {
  val queues: Map[Int, PIOQueue[T]] = Map.empty;
  val queuesList: ListBuffer[PIOQueue[T]] = ListBuffer.empty

  val queuesCounter: ListBuffer[Int] = ListBuffer.empty

  val addLock = new ReentrantLock()

  override def toString: String = {
    "{\"r\":\"" + role + "\",\"pio\":" + queuesList.foldLeft(0)(_ + _.pio) + ",\"size\":" + queuesList.foldLeft(0)(_ + _.size) + ",\"cc\":" + queuesList.foldLeft(0)(_ + _.offerCC.get()) + "}"
    //    ""
  }
  def checkIfExist(sc: T): Boolean = {
    return queuesList.filter(_.checkIfExist(sc)).size > 0
  }

  def offer(sc: T)(implicit lq: LinkedBlockingQueue[T] = null): Unit = {
    queues.get(sc.pio) match {
      case Some(q) => q.offer(sc)
      case None => {
        if (lq != null) {
          val plq = new PIOQueue(sc.pio, lq);
          plq.offer(sc)
          var index: Int = 0

          breakable {
            for (i <- 0 to queuesList.size - 1) {
              index = i
              if (queuesList(i).pio < sc.pio) {
                break
              }
            }
          }
          queuesList.insert(index, plq)
          queuesCounter.insert(index, 1)
          queues.+=((sc.pio, plq))
        } else {
          addLock.lock()
          offer(sc)(new LinkedBlockingQueue)
          addLock.unlock();
        }
      }
    }
  }

  private def wrapPoll(task: T): Option[T] = {
    task match {
      case p if p != null => Some(p)
      case _ => None
    }
  }

  def poll(filter: String = null): Option[T] = {
    for (q <- queuesList) {
      if (q.size() > 0) {
        if (filter != null && filter.length() > 0) {
          for (i <- 1 to q.size()) {
            val task = q.poll();
            if (task != null) {
              if (task.rolemark == null || (!task.rolemark.contains(filter + ","))) return wrapPoll(task);
              else //put back
              {
                q.offer(task)(true)
                //return None;
              }
            }
          }
        } else {
          val task = q.poll();
          if (task != null) {
            return wrapPoll(task);
          }
        }
      }
    }
    return None
  }

  def obtainByID(taskid: String): Option[T] = {
    for (q <- queuesList) {
      if (q.size() > 0) {
        for (i <- 1 to q.size()) {
          val task = q.poll();
          if (task != null) {
            if(task.id==taskid){
              return wrapPoll(task);
            }
            else {
              q.offer(task)(true)
            } 
          }
        }
      }
    }
    return None
  }

}
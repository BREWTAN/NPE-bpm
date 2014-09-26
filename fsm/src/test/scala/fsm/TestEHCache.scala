package fsm

import scala.collection.mutable.MutableList

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.dispatch.Futures.future
import akka.pattern.Patterns.pipe
import akka.util.Timeout


object TestEHCache {

  def main(args: Array[String]) {
    var systems: MutableList[ActorSystem] = MutableList.empty;
    for (i <- 1 to 1) {
      val system = ActorSystem("PECluster", ConfigFactory.parseString("akka.remote.netty.tcp.port = 255" + i).withFallback(ConfigFactory.load))
      systems += system;

      Cluster(system).registerOnMemberUp {
//        val store = system.actorOf(akka.actor.Props[EhCacheStorage],"ehcacheStore");

//        store ! StorageToken(1)
                Thread.sleep(2000)

        
        implicit val timeout = Timeout(60000)
 
//        store ! Put("abc","aaaaa")
//        val future = akka.pattern.ask(store, Get("abc"));

//        implicit val dispatch=system.dispatcher 
//        future onComplete {
//          case ScalaSuccess(result) => println("result="+result)
//          case ScalaFailure(failure) => println(failure)
//        }
        
        println("setok")

      }

    }
    Thread.sleep(10000);
    systems.foreach({ system =>
      system.shutdown
    })
  }

}
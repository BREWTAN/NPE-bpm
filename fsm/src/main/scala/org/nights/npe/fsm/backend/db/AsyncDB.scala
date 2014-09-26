package org.nights.npe.fsm.backend.db

import com.github.mauricio.async.db.pool.PoolConfiguration
import com.typesafe.config.ConfigFactory
import com.github.mauricio.async.db.mysql.pool.MySQLConnectionFactory
import com.github.mauricio.async.db.pool.ConnectionPool
import com.github.mauricio.async.db.Configuration
import scala.concurrent.Future
import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.RowData
import akka.actor.Actor

object ADBPool {
 
  val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  val dbUsername = config.getString("tcp-async.db.username")
  val dbPassword = config.getString("tcp-async.db.password")
  val dbPort = config.getInt("tcp-async.db.port")
  val dbName = config.getString("tcp-async.db.name")

  val dbPoolMaxObjects = config.getInt("tcp-async.db.pool.maxObjects")
  val dbPoolMaxIdle = config.getInt("tcp-async.db.pool.maxIdle")
  val dbPoolMaxQueueSize = config.getInt("tcp-async.db.pool.maxQueueSize")
 
  val configuration = new Configuration(username = dbUsername,
    port = dbPort,
    password = Some(dbPassword),
    database = Some(dbName))

  val factory = new MySQLConnectionFactory(configuration)
  val pool = new ConnectionPool(factory, new PoolConfiguration(dbPoolMaxObjects, dbPoolMaxIdle, dbPoolMaxQueueSize))

}

trait AsyncDBPool { 
  import scala.concurrent.ExecutionContext.Implicits.global
  val pool = ADBPool.pool

  def execute(query: String, values: Any*): Future[QueryResult] = {
    if (values.size > 0)
      pool.sendPreparedStatement(query, values)
    else
      pool.sendQuery(query)
  }
 
  def fetch(query: String, values: Any*): Future[Option[Seq[RowData]]] =
    execute(query, values: _*).map(_.rows)

}


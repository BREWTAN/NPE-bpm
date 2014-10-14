package org.nights.npe.mcs.akka

class LocalMessage {

}

case class QueryTasks(count: Int, obtainer: String = null, role: String = null, center: String = null,exclude:String=null)

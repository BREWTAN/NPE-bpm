package org.nights.npe.po

case class AskResult[T](retcode: Long, retObj: T, faileReason: String = null)
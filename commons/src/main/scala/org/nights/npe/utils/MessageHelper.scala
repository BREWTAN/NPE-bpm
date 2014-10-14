package org.nights.npe.utils

import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import org.nights.npe.mo.ANewProcess
import org.nights.npe.po.ContextData
import org.nights.npe.po.StateContext

object MessageHelper {
  def wrappedANewProcess(procInstId: String,submitter:String, procDefId: String, data: ContextData): Any = ConsistentHashableEnvelope(ANewProcess(procInstId, submitter,procDefId, data), procInstId)
  def wrappedTransiteState(state: StateContext): Any = ConsistentHashableEnvelope(state, state.taskInstId)
}
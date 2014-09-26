package org.nights.npe.fsm

import org.nights.npe.fsm.front.ANewProcess

import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
object MessageHelper {
  def wrappedANewProcess(procInstId: String,submitter:String, procDefId: String, data: ContextData): Any = ConsistentHashableEnvelope(ANewProcess(procInstId, submitter,procDefId, data), procInstId)
  def wrappedTransiteState(state: StateContext): Any = ConsistentHashableEnvelope(state, state.taskInstId)
}
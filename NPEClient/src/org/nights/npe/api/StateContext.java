package org.nights.npe.api;

import java.util.ArrayList;

import lombok.Data;

@Data
public class StateContext {
	@Data
	public class InterState {
		int v;
	}

	String procInstId; // 流程实例ID
	String procDefId; // 流程定义ID
	String taskInstId; // 任务实例ID
	String taskDefId; // 任务定义ID
	String taskName;
	ArrayList<ParentContext> antecessors = new ArrayList<>();// 父节点信息
	InterState internalState; // 0->新建任务，1-》任务已经被获取，2-》任务已经提交，3->任务结束
	ArrayList<String> prevStateInstIds = new ArrayList<>();// 上一次任务实例ID,
	Boolean isTerminate; // 是否为结束节点
	int nodetype=0; // 0为人工节点，1为引擎节点
}

package org.nights.npe.client.test;

import org.nights.npe.api.ContextData;
import org.nights.npe.api.ResponseStatus;
import org.nights.npe.api.SubmitStates;
import org.nights.npe.client.RestClient;

public class TestRestClient {

	public static void main(String argv[]) {
		System.out.println("kkk:");

		RestClient client = new RestClient("localhost", 8080, "/");

		// 1. new process
		{
			ResponseStatus status = client.newProcess("test.charge", "test",
					"cente", new ContextData());
			System.out.println("new status=" + status);
		}
		//2.获取任务
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SubmitStates state = client.obtainTask(null, "tany", "");
		System.out.println("obtain. state==" + state.getState());
		
		//3.提交任务
		if (state.getState() != null) {
			state.setSubmitter("tany");
			ResponseStatus status = client.submitTask(state);
			System.out.println("obtain. submit==" + status);
		}
		
		//3.提交2任务
				if (state.getState() != null) {
					state.setSubmitter("tany2");
					ResponseStatus status = client.submitTask(state);
					System.out.println("obtain. submit2==" + status);
				}

	}
}

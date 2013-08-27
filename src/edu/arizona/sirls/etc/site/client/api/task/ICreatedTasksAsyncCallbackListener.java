package edu.arizona.sirls.etc.site.client.api.task;

import java.util.List;

import edu.arizona.sirls.etc.site.shared.rpc.Task;

public interface ICreatedTasksAsyncCallbackListener {

	public void notifyExceptionCreatedTasks(Throwable caught);

	public void notifyCreatedTasks(List<Task> result);

}

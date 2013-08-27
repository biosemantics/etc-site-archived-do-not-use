package edu.arizona.sirls.etc.site.client.api.task;

import java.util.List;

import edu.arizona.sirls.etc.site.shared.rpc.Task;

public interface ISharedTasksAsyncCallbackListener {
	public void notifyExceptionSharedTasks(Throwable caught);

	public void notifySharedTasks(List<Task> result);
}

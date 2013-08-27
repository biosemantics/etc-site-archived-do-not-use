package edu.arizona.sirls.etc.site.client.api.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.Task;

public class SharedTasksAsyncCallback implements AsyncCallback<List<Task>> {
	private Set<ISharedTasksAsyncCallbackListener> listeners = new HashSet<ISharedTasksAsyncCallbackListener>();
	
	public void addListener(ISharedTasksAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ISharedTasksAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(ISharedTasksAsyncCallbackListener listener : listeners) {
			listener.notifyExceptionSharedTasks(caught);
		}
	}

	@Override
	public void onSuccess(List<Task> result) {
		for(ISharedTasksAsyncCallbackListener listener : listeners) {
			listener.notifySharedTasks(result);
		}
	}
}

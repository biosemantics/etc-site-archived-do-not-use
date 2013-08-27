package edu.arizona.sirls.etc.site.client.api.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.Task;

public class CreatedTasksAsyncCallback implements AsyncCallback<List<Task>> {
	
	private Set<ICreatedTasksAsyncCallbackListener> listeners = new HashSet<ICreatedTasksAsyncCallbackListener>();
	
	public void addListener(ICreatedTasksAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ICreatedTasksAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(ICreatedTasksAsyncCallbackListener listener : listeners) {
			listener.notifyExceptionCreatedTasks(caught);
		}
	}

	@Override
	public void onSuccess(List<Task> result) {
		for(ICreatedTasksAsyncCallbackListener listener : listeners) {
			listener.notifyCreatedTasks(result);
		}
	}
}

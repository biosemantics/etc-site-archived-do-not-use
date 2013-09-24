package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getCreatedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getSharedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void addTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);
	
}

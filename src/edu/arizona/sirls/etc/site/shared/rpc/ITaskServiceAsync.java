package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getCreatedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getSharedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void addTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);

	public void getPastTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);

	public void isResumable(AuthenticationToken authenticationToken, Task task, AsyncCallback<Boolean> callback);

	public void isCompleted(AuthenticationToken authenticationToken, Task task, AsyncCallback<Boolean> callback);

	public void hasResumable(AuthenticationToken authenticationToken, AsyncCallback<Boolean> callback);
	
	public void getResumableTasks(AuthenticationToken authenticationToken, AsyncCallback<Map<Integer, Task>> callback); 
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);

	//public void getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType, AsyncCallback<Task> callback);
	
}

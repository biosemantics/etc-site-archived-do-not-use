package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

@RemoteServiceRelativePath("task")
public interface ITaskService extends RemoteService {

	public List<Task> getAllTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getCreatedTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getSharedTasks(AuthenticationToken authenticationToken);
	
	public Task addTask(AuthenticationToken authenticationToken, Task task);
	
	//public Task getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType);
	
}

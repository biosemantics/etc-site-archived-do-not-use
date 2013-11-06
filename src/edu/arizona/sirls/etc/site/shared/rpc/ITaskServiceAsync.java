package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);
	
	public void getCreatedTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);
	
	public void getSharedTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);
	
	public void addTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> callback);

	public void getPastTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);

	public void isResumable(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Boolean>> callback);

	public void isComplete(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Boolean>> callback);

	public void hasResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void getResumableTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Map<Integer, Task>>> callback); 
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);

	//public void getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType, AsyncCallback<Task> callback);
	
	public void addShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<RPCResult<Share>> callback);
	
	public void getOwnedShares(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Share>>> callback);
	
	public void getInvitedShares(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Share>>> callback);
	
}

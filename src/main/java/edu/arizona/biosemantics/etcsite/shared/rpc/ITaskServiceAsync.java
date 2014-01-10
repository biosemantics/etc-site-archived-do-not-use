package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.Share;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);
	
	public void getOwnedTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);
	
	public void getSharedWithTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);

	public void getCompletedTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Task>>> callback);

	public void isResumable(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Boolean>> callback);

	public void isComplete(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Boolean>> callback);

	public void hasResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void getResumableTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Map<Integer, Task>>> callback); 
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);

	//public void getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType, AsyncCallback<Task> callback);
	
	public void addShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<RPCResult<Share>> callback);
	
	public void getOwnedShares(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Share>>> callback);
	
	public void getInvitedShares(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<Share>>> callback);

	public void getInvitees(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Set<ShortUser>>> callback);

	public void addOrUpdateShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<RPCResult<Share>> callback);

	public void removeMeFromShare(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);
	
	public void updateShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<RPCResult<Share>> callback);
	
	public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

	public void getTask(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<Task>> callback);

	public void getInviteesForOwnedTasks(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Map<Task, Set<ShortUser>>>> callback);
	
}

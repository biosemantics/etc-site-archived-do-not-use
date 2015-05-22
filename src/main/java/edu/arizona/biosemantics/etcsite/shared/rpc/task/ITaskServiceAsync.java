package edu.arizona.biosemantics.etcsite.shared.rpc.task;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getOwnedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getSharedWithTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);

	public void getCompletedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);

	public void isResumable(AuthenticationToken authenticationToken, Task task, AsyncCallback<Boolean> callback);

	public void isComplete(AuthenticationToken authenticationToken, Task task, AsyncCallback<Boolean> callback);

	public void hasResumable(AuthenticationToken authenticationToken, AsyncCallback<Boolean> callback);
	
	public void getResumableOrFailedTasks(AuthenticationToken authenticationToken, AsyncCallback<Map<Integer, Task>> callback); 
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);

	//public void getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType, AsyncCallback<Task> callback);
	
	public void addShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<Share> callback);
	
	public void getOwnedShares(AuthenticationToken authenticationToken, AsyncCallback<List<Share>> callback);
	
	public void getInvitedShares(AuthenticationToken authenticationToken, AsyncCallback<List<Share>> callback);

	public void getInvitees(AuthenticationToken authenticationToken, Task task, AsyncCallback<Set<ShortUser>> callback);

	public void addOrUpdateShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<Share> callback);

	public void removeMeFromShare(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);
	
	public void updateShare(AuthenticationToken authenticationToken, Share share, AsyncCallback<Share> callback);
	
	public void cancelTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);

	public void getTask(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<Task> callback);

	public void getInviteesForOwnedTasks(AuthenticationToken authenticationToken, AsyncCallback<Map<Task, Set<ShortUser>>> callback);
	
}

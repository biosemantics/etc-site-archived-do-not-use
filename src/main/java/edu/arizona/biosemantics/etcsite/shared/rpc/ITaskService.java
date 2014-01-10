package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.Share;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

@RemoteServiceRelativePath("task")
public interface ITaskService extends RemoteService {

	public RPCResult<List<Task>> getAllTasks(AuthenticationToken authenticationToken);
	
	public RPCResult<List<Task>> getOwnedTasks(AuthenticationToken authenticationToken);
	
	public RPCResult<List<Task>> getSharedWithTasks(AuthenticationToken authenticationToken);
	
	public RPCResult<List<Task>> getCompletedTasks(AuthenticationToken authenticationToken);
	
	public RPCResult<Boolean> isResumable(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Boolean> isComplete(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Boolean> hasResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<Map<Integer, Task>> getResumableTasks(AuthenticationToken authenticationToken);
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task);
	
	//public Task getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType);
	
	public RPCResult<List<Share>> getOwnedShares(AuthenticationToken authenticationToken);
	
	public RPCResult<List<Share>> getInvitedShares(AuthenticationToken authenticationToken);

	public RPCResult<Share> addShare(AuthenticationToken authenticationToken, Share share);
	
	public RPCResult<Set<ShortUser>> getInvitees(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Share> addOrUpdateShare(AuthenticationToken authenticationToken, Share share);
	
	public RPCResult<Void> removeMeFromShare(AuthenticationToken authenticationToken, Task task);

	public RPCResult<Share> updateShare(AuthenticationToken authenticationToken, Share share);
	
	public RPCResult<Void> cancelTask(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Task> getTask(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<Map<Task, Set<ShortUser>>> getInviteesForOwnedTasks(AuthenticationToken authenticationToken);
	
}

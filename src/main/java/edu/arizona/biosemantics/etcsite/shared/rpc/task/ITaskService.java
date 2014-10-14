package edu.arizona.biosemantics.etcsite.shared.rpc.task;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("task")
public interface ITaskService extends RemoteService {

	public List<Task> getAllTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getOwnedTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getSharedWithTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getCompletedTasks(AuthenticationToken authenticationToken);
	
	public boolean isResumable(AuthenticationToken authenticationToken, Task task);
	
	public boolean isComplete(AuthenticationToken authenticationToken, Task task);
	
	public boolean hasResumable(AuthenticationToken authenticationToken);
	
	public Map<Integer, Task> getResumableOrFailedTasks(AuthenticationToken authenticationToken);
	
	//public void cancelTask(AuthenticationToken authenticationToken, Task task);
	
	//public Task getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType);
	
	public List<Share> getOwnedShares(AuthenticationToken authenticationToken);
	
	public List<Share> getInvitedShares(AuthenticationToken authenticationToken);

	public Share addShare(AuthenticationToken authenticationToken, Share share);
	
	public Set<ShortUser> getInvitees(AuthenticationToken authenticationToken, Task task);
	
	public Share addOrUpdateShare(AuthenticationToken authenticationToken, Share share);
	
	public void removeMeFromShare(AuthenticationToken authenticationToken, Task task);

	public Share updateShare(AuthenticationToken authenticationToken, Share share);
	
	public void cancelTask(AuthenticationToken authenticationToken, Task task) throws Exception;
	
	public Task getTask(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public Map<Task, Set<ShortUser>> getInviteesForOwnedTasks(AuthenticationToken authenticationToken);
	
}

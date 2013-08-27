package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

@RemoteServiceRelativePath("task")
public interface ITaskService extends RemoteService {

	public List<Task> getAllTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getCreatedTasks(AuthenticationToken authenticationToken);
	
	public List<Task> getSharedTasks(AuthenticationToken authenticationToken);
	
}

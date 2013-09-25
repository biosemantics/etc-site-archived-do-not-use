package edu.arizona.sirls.etc.site.server.rpc;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskDAO;

public class TaskService extends RemoteServiceServlet implements ITaskService {

	private static final long serialVersionUID = -3080921351813858330L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public List<Task> getAllTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				result = TaskDAO.getInstance().getUsersTasks(authenticationToken.getUsername());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<Task> getCreatedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				result = TaskDAO.getInstance().getUsersTasks(authenticationToken.getUsername());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<Task> getSharedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		return result;
	}

	@Override
	public void addTask(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				task = TaskDAO.getInstance().addTask(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	


	/*@Override
	public Task getLatestResumableTask(AuthenticationToken authenticationToken, TaskTypeEnum taskType) {
		Task result = null;
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				result = TaskDAO.getInstance().getLatestResumableTask(authenticationToken.getUsername(), taskType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}*/

}

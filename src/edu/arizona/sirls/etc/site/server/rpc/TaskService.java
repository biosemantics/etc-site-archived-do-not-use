package edu.arizona.sirls.etc.site.server.rpc;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dev.util.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.Task;
import edu.arizona.sirls.etc.site.shared.rpc.TaskType;

public class TaskService extends RemoteServiceServlet implements ITaskService {

	private static final long serialVersionUID = -3080921351813858330L;
	private IAuthenticationService authenticationService = new AuthenticationService();

	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public List<Task> getAllTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			Calendar calendar = Calendar.getInstance();
			calendar.set(2013, 7, 22, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.MATRIX_GENERATION, "My example task", 20));
			calendar.set(2013, 7, 21, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.MATRIX_GENERATION, "My example task2", 22));
			calendar.set(2013, 7, 20, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.TREE_GENERATION, "A shared task", 55));
			Lists.sort(result);
		}
		return result;
	}

	@Override
	public List<Task> getCreatedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			Calendar calendar = Calendar.getInstance();
			calendar.set(2013, 7, 22, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.MATRIX_GENERATION, "My example task", 20));
			calendar.set(2013, 7, 21, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.MATRIX_GENERATION, "My example task2", 22));
			Lists.sort(result);
		}
		return result;
	}

	@Override
	public List<Task> getSharedTasks(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			Calendar calendar = Calendar.getInstance();
			calendar.set(2013, 7, 20, 13, 23);
			result.add(new Task(calendar.getTime(), TaskType.TREE_GENERATION, "A shared task", 55));
			Lists.sort(result);
		}
		return result;
	}

}

package edu.arizona.biosemantics.etcsite.client.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;

public class ResumableTaskFinder {

	private Timer resumableTasksTimer;
	private ITaskServiceAsync taskService;
	private EventBus tasksBus;
	private int resumableTasksTime;

	@Inject
	public ResumableTaskFinder(final ITaskServiceAsync taskService, @Named("Tasks") final EventBus tasksBus, @Named("CheckResumables")int resumableTasksTime) {
		this.taskService = taskService;
		this.tasksBus = tasksBus;
		this.resumableTasksTimer = new Timer() {
	        public void run() {
	        	if(Authentication.getInstance().isSet()) {
		        	taskService.getResumableOrFailedTasks(Authentication.getInstance().getToken(), new AsyncCallback<Map<Integer, Task>>() {
						@Override
						public void onSuccess(Map<Integer, Task> result) {					
							Map<Integer, Task> resumableTasks = new HashMap<Integer, Task>();
							Map<Integer, Task> failedTasks = new HashMap<Integer, Task>();
							for(Integer id : result.keySet()) {
								Task task = result.get(id);
								if(task.isResumable())
									resumableTasks.put(id, task);
								if(task.isFailed())
									failedTasks.put(id, task);
							}
							
							tasksBus.fireEvent(new ResumableTasksEvent(resumableTasks));
							tasksBus.fireEvent(new FailedTasksEvent(failedTasks));
						}
						@Override
						public void onFailure(Throwable caught) {
							//Usually don't want to show this to the user, as it may just have happened 
							// e.g. because no connection could be obtained to retrieve the running tasks or similarly
							// confuses because it happens from user's perspective *randomly*
							//Alerter.failedToGetResumableTasks(caught);
						}
		    		});
	        	}
	        }
		};
		this.resumableTasksTime = resumableTasksTime;
	}
	
	public void start() {
		this.resumableTasksTimer.scheduleRepeating(resumableTasksTime);
	}
	
	public void stop() {
		this.resumableTasksTimer.cancel();
	}

}

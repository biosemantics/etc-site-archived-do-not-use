package edu.arizona.sirls.etc.site.client;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class TaskManager {

	private Task activeTask; 
	
	public Task getActiveTask() {
		return activeTask;
	}
	
	public boolean hasActiveTask() {
		return this.activeTask != null;
	}
	
	public void setActiveTask(Task activeTask) {
		this.activeTask = activeTask;
	}

	public void removeActiveTask() {
		this.activeTask = null;
	}
	
}
package edu.arizona.sirls.etc.site.client;

import edu.arizona.sirls.etc.site.shared.rpc.AbstractTaskRun;

public class TaskManager {

	private AbstractTaskRun activeTaskRun; 
	
	public AbstractTaskRun getActiveTaskRun() {
		return activeTaskRun;
	}
	
	public boolean hasActiveTaskRun() {
		return this.activeTaskRun != null;
	}
	
	public void setActiveTaskRun(AbstractTaskRun taskRun) {
		this.activeTaskRun = taskRun;
	}

	public void removeActiveTaskConfiguration() {
		this.activeTaskRun = null;
	}
	
}

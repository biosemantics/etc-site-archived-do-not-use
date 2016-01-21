package edu.arizona.biosemantics.etcsite.client.common;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;

public interface HasTask {

	public Task getTask();
	
	public void setTask(Task task);

	public boolean hasTask();
	
}

package edu.arizona.sirls.etc.site.client.event;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface ITaskEvent {

	public Task getTask();

	public void setTask(Task task);
	
	public boolean hasTask();
	
}

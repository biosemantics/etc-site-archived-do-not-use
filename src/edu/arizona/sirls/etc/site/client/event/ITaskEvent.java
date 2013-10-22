package edu.arizona.sirls.etc.site.client.event;

import edu.arizona.sirls.etc.site.shared.rpc.AbstractTaskRun;

public interface ITaskEvent <T extends AbstractTaskRun> {

	public T getTaskConfiguration();

	public void setTaskConfiguration(T task);
	
	public boolean hasTaskConfiguration();
	
}

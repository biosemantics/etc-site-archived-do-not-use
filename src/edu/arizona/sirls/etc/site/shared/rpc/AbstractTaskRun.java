package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;

import edu.arizona.sirls.etc.site.shared.rpc.db.AbstractTaskConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public abstract class AbstractTaskRun<T extends AbstractTaskConfiguration> implements Serializable {

	private static final long serialVersionUID = 8267646351040982300L;
	private Task task;
	
	public AbstractTaskRun() { }
	
	public AbstractTaskRun(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	public abstract T getConfiguration();
	
	public abstract void setConfiguration(T configuration);
	
}

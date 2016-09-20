package edu.arizona.biosemantics.etcsite.shared.rpc;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class HasTaskException extends Exception {

	private Task task;
	
	public HasTaskException() { }
		
	public HasTaskException(String message) {
		super(message);
	}
	
	public HasTaskException(Task task) {
		this.task = task;
	}
	
	public HasTaskException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
	
}

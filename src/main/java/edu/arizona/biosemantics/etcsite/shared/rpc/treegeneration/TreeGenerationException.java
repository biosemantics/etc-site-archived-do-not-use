package edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TreeGenerationException extends Exception {

	private Task task;
	
	public TreeGenerationException() { }
	
	public TreeGenerationException(String message) {
		super(message);
	}
	
	public TreeGenerationException(Task task) {
		this.task = task;
	}
	
	public TreeGenerationException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

}

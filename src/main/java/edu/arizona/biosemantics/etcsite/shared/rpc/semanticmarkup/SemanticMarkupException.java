package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupException extends Exception {

	private Task task;
	
	public SemanticMarkupException() { }
	
	public SemanticMarkupException(String message) {
		super(message);
	}
	
	public SemanticMarkupException(Task task) {
		this.task = task;
	}
	
	public SemanticMarkupException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

}

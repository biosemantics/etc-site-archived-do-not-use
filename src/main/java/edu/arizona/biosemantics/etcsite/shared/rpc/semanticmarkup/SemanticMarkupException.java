package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupException extends Exception {

	private Task task;
	
	private SemanticMarkupException() { }
	
	public SemanticMarkupException(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

}

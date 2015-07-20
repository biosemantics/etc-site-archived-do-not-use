package edu.arizona.biosemantics.etcsite.shared.rpc.ontologize;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class OntologizeException extends Exception {

	private Task task;
	
	public OntologizeException() { }
	
	public OntologizeException(String message) {
		super(message);
	}
	
	public OntologizeException(Task task) {
		this.task = task;
	}
	
	public OntologizeException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public OntologizeException(Exception e) {
		super(e);
	}

	public Task getTask() {
		return task;
	}

}

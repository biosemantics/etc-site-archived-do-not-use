package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationException extends Exception {

	private Task task;
	
	public MatrixGenerationException() { }
		
	public MatrixGenerationException(String message) {
		super(message);
	}
	
	public MatrixGenerationException(Task task) {
		this.task = task;
	}
	
	public MatrixGenerationException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
}

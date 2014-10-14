package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationException extends Exception {

	private Task task;
	
	public MatrixGenerationException() { }
	
	public MatrixGenerationException(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
}

package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class MatrixGenerationPlace extends Place {

	private Task task;
	
	public MatrixGenerationPlace(Task task) {
		super();
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	
	
}

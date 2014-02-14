package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class HasTaskPlace extends Place {

	private Task task;
	
	public HasTaskPlace() {
		
	}
	
	public HasTaskPlace(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) { 
		this.task = task;
	}
	
	public boolean hasTask() {
		return task != null;
	}
	
}

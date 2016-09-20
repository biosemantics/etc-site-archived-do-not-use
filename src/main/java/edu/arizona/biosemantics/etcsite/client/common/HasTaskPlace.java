package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class HasTaskPlace extends Place implements HasTask {

	private Task task;
	
	public HasTaskPlace() {
		
	}
	
	public HasTaskPlace(Task task) {
		this.task = task;
	}
	
	@Override
	public Task getTask() {
		return task;
	}
	
	@Override
	public void setTask(Task task) { 
		this.task = task;
	}
	
	@Override
	public boolean hasTask() {
		return task != null;
	}
	
}

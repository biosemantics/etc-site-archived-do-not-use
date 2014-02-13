package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupPlace extends Place implements MenuPlace {

	private Task task;
	
	public SemanticMarkupPlace(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
}

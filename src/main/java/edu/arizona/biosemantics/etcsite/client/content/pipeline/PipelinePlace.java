package edu.arizona.biosemantics.etcsite.client.content.pipeline;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class PipelinePlace extends HasTaskPlace implements MenuPlace {

	public PipelinePlace() {
		super(null);
	}
	
	public PipelinePlace(Task task) {
		super(task);
	}
	
}
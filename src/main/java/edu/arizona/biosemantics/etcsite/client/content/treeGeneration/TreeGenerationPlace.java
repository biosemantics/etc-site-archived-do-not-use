package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.Place;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class TreeGenerationPlace extends HasTaskPlace {

	public TreeGenerationPlace() {
		super(null);
	}
	
	public TreeGenerationPlace(Task task) {
		super(task);
	}
	
}

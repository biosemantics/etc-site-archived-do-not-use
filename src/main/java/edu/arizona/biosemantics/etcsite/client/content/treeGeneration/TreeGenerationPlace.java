package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TreeGenerationPlace extends HasTaskPlace implements RequiresAuthenticationPlace  {

	public TreeGenerationPlace(Task task) {
		super(task);
	}
	
	
}

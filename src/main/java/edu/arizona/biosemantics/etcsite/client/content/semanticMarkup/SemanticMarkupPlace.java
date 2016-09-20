package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupPlace extends HasTaskPlace implements RequiresAuthenticationPlace {

	public SemanticMarkupPlace(Task task) {
		super(task);
	}
	
}

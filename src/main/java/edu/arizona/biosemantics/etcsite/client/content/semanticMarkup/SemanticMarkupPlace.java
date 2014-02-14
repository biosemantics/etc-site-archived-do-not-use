package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupPlace extends HasTaskPlace implements MenuPlace {

	public SemanticMarkupPlace(Task task) {
		super(task);
	}
	
}

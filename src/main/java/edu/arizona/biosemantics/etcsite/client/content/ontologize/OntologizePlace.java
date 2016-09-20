package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class OntologizePlace extends HasTaskPlace implements RequiresAuthenticationPlace  {

	public OntologizePlace(Task task) {
		super(task);
	}

	public OntologizePlace() {
		super();
	}
	
	
}

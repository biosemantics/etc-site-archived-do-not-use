package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationPlace extends HasTaskPlace implements RequiresAuthenticationPlace {

	public MatrixGenerationPlace(Task task) {
		super(task);
	}
	
	
}

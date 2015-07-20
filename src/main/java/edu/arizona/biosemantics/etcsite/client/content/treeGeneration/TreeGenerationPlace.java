package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TreeGenerationPlace extends HasTaskPlace implements RequiresAuthenticationPlace  {

	public TreeGenerationPlace(Task task) {
		super(task);
	}
	
}

package edu.arizona.biosemantics.etcsite.client.content.pipeline;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class PipelinePlace extends HasTaskPlace  {

	public PipelinePlace() {
		super(null);
	}
	
	public PipelinePlace(Task task) {
		super(task);
	}
	
}
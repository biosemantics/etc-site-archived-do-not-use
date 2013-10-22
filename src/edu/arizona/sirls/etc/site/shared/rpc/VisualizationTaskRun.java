package edu.arizona.sirls.etc.site.shared.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public class VisualizationTaskRun extends AbstractTaskRun<VisualizationConfiguration> {

	private static final long serialVersionUID = -1306553723599843896L;
	private VisualizationConfiguration configuration;

	public VisualizationTaskRun() { }
	
	public VisualizationTaskRun(Task task, VisualizationConfiguration configuration) {
		super(task);
		this.configuration = configuration;
	}

	@Override
	public VisualizationConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(VisualizationConfiguration configuration) {
		this.configuration = configuration;
	}	
	
}

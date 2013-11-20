package edu.arizona.sirls.etc.site.shared.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.db.SemanticMarkupConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class SemanticMarkupTaskRun extends AbstractTaskRun<SemanticMarkupConfiguration> {

	private static final long serialVersionUID = -4327049203708241114L;
	private SemanticMarkupConfiguration configuration;
	
	public SemanticMarkupTaskRun() { }
	
	public SemanticMarkupTaskRun(SemanticMarkupConfiguration configuration, Task task) {
		super(task);
		this.configuration = configuration;
	}

	@Override
	public SemanticMarkupConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(SemanticMarkupConfiguration configuration) {
		this.configuration = configuration;
	}
	

}

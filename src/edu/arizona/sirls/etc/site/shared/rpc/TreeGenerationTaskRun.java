package edu.arizona.sirls.etc.site.shared.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

public class TreeGenerationTaskRun extends AbstractTaskRun<TreeGenerationConfiguration> {

	private static final long serialVersionUID = -8274679461046367533L;
	private TreeGenerationConfiguration configuration;

	public TreeGenerationTaskRun() { }
	
	public TreeGenerationTaskRun(Task task, TreeGenerationConfiguration configuration) {
		super(task);
		this.configuration = configuration;
	}

	@Override
	public TreeGenerationConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(TreeGenerationConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
}

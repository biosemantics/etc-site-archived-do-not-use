package edu.arizona.sirls.etc.site.shared.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class MatrixGenerationTaskRun extends AbstractTaskRun<MatrixGenerationConfiguration> {

	private static final long serialVersionUID = -4327049203708241114L;
	private MatrixGenerationConfiguration configuration;
	
	public MatrixGenerationTaskRun() { }
	
	public MatrixGenerationTaskRun(MatrixGenerationConfiguration configuration, Task task) {
		super(task);
		this.configuration = configuration;
	}

	@Override
	public MatrixGenerationConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(MatrixGenerationConfiguration configuration) {
		this.configuration = configuration;
	}
	

}

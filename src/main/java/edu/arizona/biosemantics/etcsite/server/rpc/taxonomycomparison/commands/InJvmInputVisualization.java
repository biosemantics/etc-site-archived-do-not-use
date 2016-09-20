package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerException;
import edu.arizona.biosemantics.euler2.EulerShow;

public class InJvmInputVisualization implements InputVisualization {

	private String inputFile;
	private String workingDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmInputVisualization(String inputFile, String workingDir) {
		this.inputFile = inputFile;
		this.workingDir = workingDir;
	}
	
	@Override
	public Void call() throws EulerException {
		try {
			log(LogLevel.DEBUG, "Running euler input viz: input " + inputFile + " ; workingDir " + workingDir);
			EulerShow euler = new EulerShow();
			euler.setWorkingDir(workingDir);
			euler.setInputFile(inputFile);
			euler.setName("iv");
			euler.run();
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Input Visualization failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new EulerException("Input Visualization failed");
		}
		return null;
	}

	@Override
	public void destroy() { }

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}

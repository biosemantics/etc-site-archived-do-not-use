package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerException;

public class InJvmConsistencyCheck implements ConsistencyCheck {

	private String inputFile;
	private String workingDir;
	private String outputDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmConsistencyCheck(String inputFile, String workingDir, String outputDir) {
		this.inputFile = inputFile;
		this.workingDir = workingDir;
		this.outputDir = outputDir;
	}
	
	@Override
	public Boolean call() throws EulerException {
		String commandLineOutput = "";
		try {
			log(LogLevel.DEBUG, "Running euler consistency check: input " + inputFile + " ; workingDir " + workingDir);
			EulerAlign eulerAlign = new EulerAlign();
			eulerAlign.setWorkingDir(workingDir);
			eulerAlign.setInputFile(inputFile);
			eulerAlign.setOutputDirectory(outputDir);
			eulerAlign.setConsistency(true);
			commandLineOutput = eulerAlign.run();
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Euler consistency check failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new EulerException("Execution failed: " + commandLineOutput);
		}
		return !commandLineOutput.contains("inconsistent");
	}

	@Override
	public void destroy() { }

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}
}

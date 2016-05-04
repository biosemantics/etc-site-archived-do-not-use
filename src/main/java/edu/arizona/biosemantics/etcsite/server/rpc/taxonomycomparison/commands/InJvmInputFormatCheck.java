package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.euler2.EulerCheck;
import edu.arizona.biosemantics.euler2.EulerException;

public class InJvmInputFormatCheck implements InputFormatCheck {

	private String inputFile;
	private String outputDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmInputFormatCheck(String inputFile, String outputDir) {
		this.inputFile = inputFile;
		this.outputDir = outputDir;
	}
	
	@Override
	public Boolean call() throws EulerException {
		String commandLineOutput = "";
		try {
			log(LogLevel.DEBUG, "Running euler format check: input " + inputFile + " output: " + outputDir);
			EulerCheck eulerCheck = new EulerCheck();
			eulerCheck.setInputFile(inputFile);
			commandLineOutput = eulerCheck.run();
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Euler format check failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new EulerException("Execution failed: " + commandLineOutput);
		}
		return true;
		//return !commandLineOutput.contains("..something");
	}

	@Override
	public void destroy() { }

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}
}

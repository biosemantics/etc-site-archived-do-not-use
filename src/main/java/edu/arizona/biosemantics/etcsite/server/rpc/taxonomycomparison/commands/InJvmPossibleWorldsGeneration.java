package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerException;
import edu.arizona.biosemantics.euler2.EulerShow;
import edu.arizona.biosemantics.euler2.Reasoner;

public class InJvmPossibleWorldsGeneration implements PossibleWorldsGeneration {

	private String inputFile;
	private String outputDir;
	private String workingDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmPossibleWorldsGeneration(String inputFile, String outputDir, String workingDir) {
		this.inputFile = inputFile;
		this.outputDir = outputDir;
		this.workingDir = workingDir;
	}
	
	@Override
	public Void call() throws EulerException {
		String commandLineOutput = "";
		try {
			log(LogLevel.DEBUG, "Running euler: input " + inputFile + " ; workingDir " + workingDir);
			
			EulerAlign euler = new EulerAlign();
			euler.setInputFile(inputFile);
			euler.setWorkingDir(workingDir);
			euler.setOutputDirectory(outputDir);
			euler.setReasoner(Reasoner.GRINGO);
			commandLineOutput = euler.run();
			commandLineOutput += "\n\n";
			EulerShow show = new EulerShow();
			show.setWorkingDir(workingDir);
			show.setOutputDirectory(outputDir);
			show.setName("pw"); // Generate possible worlds pdfs.
			commandLineOutput += show.run();
			show.setName("sv"); // Generate aggregate pdfs.
			show.run();
			
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Euler align failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new EulerException("Execution failed: " + commandLineOutput);
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

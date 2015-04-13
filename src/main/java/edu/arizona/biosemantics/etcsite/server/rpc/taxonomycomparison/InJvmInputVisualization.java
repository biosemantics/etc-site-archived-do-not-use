package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler.Euler;

public class InJvmInputVisualization implements InputVisualization {

	private String inputFile;
	private String workingDir;
	private String outputDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmInputVisualization(String inputFile, String workingDir, String ouputDir) {
		this.inputFile = inputFile;
		this.workingDir = workingDir;
		this.outputDir = ouputDir;
	}
	
	@Override
	public Void call() throws TaxonomyComparisonException {
		try {
			log(LogLevel.DEBUG, "Running euler input viz: input " + inputFile + " ; workingDir " + workingDir + " ; output " + outputDir);
			Euler euler = new Euler();
			euler.setInputFile(inputFile);
			euler.setWorkingDir(workingDir);
			euler.setOutputDir(outputDir);
			euler.setInputVisualization(true);
			euler.run();
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Taxonomy Comparison failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new TaxonomyComparisonException();
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

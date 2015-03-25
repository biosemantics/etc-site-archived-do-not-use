package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler.Euler;

public class InJvmMIRGeneration implements MIRGeneration {

	private String inputFile;
	private String outputDir;
	
	private boolean executedSuccessfully = false;
	
	public InJvmMIRGeneration(String inputFile, String ouputDir) {
		this.inputFile = inputFile;
		this.outputDir = ouputDir;
	}
	
	@Override
	public Void call() throws TaxonomyComparisonException {
		try {
			log(LogLevel.DEBUG, "Running euler: input " + inputFile + " ; output " + outputDir);
			Euler euler = new Euler();
			euler.setInputFile(inputFile);
			euler.setOutputDir(outputDir);
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

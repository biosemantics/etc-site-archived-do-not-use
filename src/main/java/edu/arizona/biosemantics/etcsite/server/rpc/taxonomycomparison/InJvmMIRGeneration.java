package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerShow;
import edu.arizona.biosemantics.euler2.Reasoner;

public class InJvmMIRGeneration implements MIRGeneration {

	private String inputFile;
	private String workingDir;
	
	private boolean executedSuccessfully = false;
	
	
	public InJvmMIRGeneration(String inputFile, String workingDir) {
		this.inputFile = inputFile;
		this.workingDir = workingDir;
	}
	
	@Override
	public Void call() throws TaxonomyComparisonException {
		try {
			log(LogLevel.DEBUG, "Running euler: input " + inputFile + " ; workingDir " + workingDir);
			EulerAlign euler = new EulerAlign();
			euler.setInputFile(inputFile);
			euler.setWorkingDir(workingDir);
			euler.setReasoner(Reasoner.GRINGO);
			euler.run();
			EulerShow show = new EulerShow();
			show.setWorkingDir(workingDir);
			show.setName("pw"); // Generate possible worlds pdfs.
			show.run();
			show.setName("sv"); // Generate aggregate pdfs.
			show.run();
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

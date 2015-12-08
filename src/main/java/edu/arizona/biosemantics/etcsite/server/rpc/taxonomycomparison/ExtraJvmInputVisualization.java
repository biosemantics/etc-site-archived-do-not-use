package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerShow;

public class ExtraJvmInputVisualization extends ExtraJvmCallable<Void> implements InputVisualization {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				String workingDir = args[0];
				String inputFile = args[1];
				EulerShow euler = new EulerShow();
				euler.setWorkingDir(workingDir);
				euler.setInputFile(inputFile);
				euler.setName("iv");
				euler.run();
			} catch (Throwable t) {
				System.exit(-1);
			}
		}
		
	}

	private String workingDir;
	private String inputFile;
	
	public ExtraJvmInputVisualization(String inputFile, String workingDir) {
		this.workingDir = workingDir;
		this.inputFile = inputFile;
		
		this.setArgs(createArgs());
		if(!Configuration.taxonomyComparison_xms.isEmpty()) 
			this.setXms(Configuration.taxonomyComparison_xms);
		if(!Configuration.taxonomyComparison_xmx.isEmpty()) 
			this.setXmx(Configuration.taxonomyComparison_xmx);
		
		//could be reduced to only libraries relevant to taxonomy comparison
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(MainWrapper.class);
	}
	
	private String[] createArgs() {
		String[] args = { workingDir, inputFile };
		return args;
	}

	@Override
	public Void createReturn() throws TaxonomyComparisonException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Taxonomy Comparison Input Visualization failed.");
			throw new TaxonomyComparisonException();
		}
		return null;
	}
}
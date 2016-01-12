package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerShow;
import edu.arizona.biosemantics.euler2.Reasoner;

public class ExtraJvmMIRGeneration extends ExtraJvmCallable<Void> implements MIRGeneration {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				String workingDir = args[0];
				String inputFile = args[1];
				String outputDirectory = args[2];
				EulerAlign euler = new EulerAlign();
				euler.setInputFile(inputFile);
				euler.setWorkingDir(workingDir);
				euler.setReasoner(Reasoner.GRINGO);
				euler.setOutputDirectory(outputDirectory);
				euler.run();
				EulerShow show = new EulerShow();
				show.setWorkingDir(workingDir);
				show.setOutputDirectory(outputDirectory);
				show.setName("pw"); // Generate possible worlds pdfs.
				show.run();
				show.setName("sv"); // Generate aggregate pdfs.
				show.run();
			} catch (Throwable t) {
				System.exit(-1);
			}
		}
		
	}

	private String workingDir;
	private String inputFile;
	private String outputDirectory;
	
	public ExtraJvmMIRGeneration(String inputFile, String outputDirectory, String workingDir) {
		this.workingDir = workingDir;
		this.inputFile = inputFile;
		this.outputDirectory = outputDirectory;
		
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
		String[] args = { workingDir, inputFile, outputDirectory };
		return args;
	}

	@Override
	public Void createReturn() throws TaxonomyComparisonException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Taxonomy Comparison MIR Generation failed.");
			throw new TaxonomyComparisonException();
		}
		return null;
	}
}
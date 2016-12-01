package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerException;
import edu.arizona.biosemantics.euler2.EulerShow;

public class ExtraJvmInputVisualization extends ExtraJvmCallable<Void> implements InputVisualization {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				String workingDir = args[0];
				String inputFile = args[1];
				String outputDir = args[2];
				EulerShow euler = new EulerShow();
				euler.setWorkingDir(workingDir);
				euler.setInputFile(inputFile);
				euler.setOutputDirectory(outputDir);
				euler.setName("iv");
				euler.run();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
		
	}

	private String workingDir;
	private String inputFile;
	private String outputDir;
	
	public ExtraJvmInputVisualization(String inputFile, String outputDir, String workingDir) {
		this.workingDir = workingDir;
		this.inputFile = inputFile;
		this.outputDir = outputDir;
		
		this.addPathEnvironment(edu.arizona.biosemantics.euler2.Configuration.eulerPath + ":" +
				edu.arizona.biosemantics.euler2.Configuration.eulerPath + "src-el:" +
				edu.arizona.biosemantics.euler2.Configuration.eulerPath + "bbox-lattice:" + 
				edu.arizona.biosemantics.euler2.Configuration.eulerPath + "default-stylesheet");
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
		String[] args = { workingDir, inputFile, outputDir };
		return args;
	}

	@Override
	public Void createReturn() throws EulerException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Input Visualization failed.");
			throw new EulerException("Input Visualization failed.");
		}
		return null;
	}
}
package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import org.python.core.PyString;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerException;
import edu.arizona.biosemantics.euler2.EulerShow;
import edu.arizona.biosemantics.euler2.Reasoner;

public class ExtraJvmPossibleWorldsGeneration extends ExtraJvmCallable<Void> implements PossibleWorldsGeneration {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				//args = new String[3];
				//args[0] = "/home/thomas/euler-test/workdir";
				//args[1] = "/home/thomas/euler-test/input.txt";
				//args[2] = "/home/thomas/euler-test/workdir/out";
				
				String pythonWorkingDir = args[0];
				String inputFile = args[1];
				String outputDir = args[2];
				
				String commandLineOutput = "";
				EulerAlign euler = new EulerAlign();
				euler.setInputFile(inputFile);
				euler.setWorkingDir(pythonWorkingDir);
				euler.setOutputDirectory(outputDir);
				euler.setReasoner(Reasoner.GRINGO);
				commandLineOutput = euler.run();
				commandLineOutput += "\n\n";
				EulerShow show = new EulerShow();
				show.setWorkingDir(pythonWorkingDir);
				show.setOutputDirectory(outputDir);
				show.setName("pw"); // Generate possible worlds pdfs.
				commandLineOutput += show.run();
				show.setName("sv"); // Generate aggregate pdfs.
				show.run();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
		
	}

	private String pythonWorkingDir;
	private String inputFile;
	private String outputDir;
	
	public ExtraJvmPossibleWorldsGeneration(String inputFile, String outputDir, String pythonWorkingDir) {
		this.pythonWorkingDir = pythonWorkingDir;
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
		this.setWorkingDir(pythonWorkingDir);
		
		//could be reduced to only libraries relevant to taxonomy comparison
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(MainWrapper.class);
	}
	
	private String[] createArgs() {
		String[] args = { pythonWorkingDir, inputFile, outputDir };
		return args;
	}

	@Override
	public Void createReturn() throws EulerException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Euler align failed.");
			throw new EulerException("Euler align failed.");
		}
		return null;
	}
}
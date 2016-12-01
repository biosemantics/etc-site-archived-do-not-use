package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.euler2.EulerAlign;
import edu.arizona.biosemantics.euler2.EulerException;

public class ExtraJvmConsistencyCheck extends ExtraJvmCallable<Boolean> implements ConsistencyCheck {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				String workingDir = args[0];
				String inputFile = args[1];
				String outputDir = args[2];
				
				EulerAlign eulerAlign = new EulerAlign();
				eulerAlign.setWorkingDir(workingDir);
				eulerAlign.setInputFile(inputFile);
				eulerAlign.setOutputDirectory(outputDir);
				eulerAlign.setConsistency(true);
				String commandLineOutput = eulerAlign.run();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private String workingDir;
	private String inputFile;
	private String outputDir;
	
	public ExtraJvmConsistencyCheck(String inputFile, String workingDir, String outputDir) {
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
	public Boolean createReturn() throws EulerException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Consistency check failed.");
			throw new EulerException("Consistency check failed.");
		}
		
		File outputFile = new File(outputDir + File.separator + "logs" + File.separator + "input.stdout");
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "UTF8"))) {
			String line = "";
			while((line = reader.readLine()) != null) {
				if(line.contains("inconsistent")) 
					return false;
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Consistency Check: Could not read output.", e);
			throw new EulerException("Consistency Check: Could not read output.");
		}
		//File outputFile = new File(outputDir + File.separator + "logs" + File.separator + "input.stderr");
		//try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), "UTF8"))) {
		//	
		//}
		return true;
	}
}

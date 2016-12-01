package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.euler2.EulerCheck;
import edu.arizona.biosemantics.euler2.EulerException;

public class ExtraJvmInputFormatCheck extends ExtraJvmCallable<Boolean> implements InputFormatCheck {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				String inputFile = args[0];
				String outputFile = args[1];
				EulerCheck eulerCheck = new EulerCheck();
				eulerCheck.setInputFile(inputFile);
				String commandLineOutput = eulerCheck.run();
				
				try(Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile)), "UTF8"))) {
					out.append(commandLineOutput);
					out.flush();
				}
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
	}

	private String inputFile;
	private String outputFile;
	
	public ExtraJvmInputFormatCheck(String inputFile, String outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		
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
		String[] args = { inputFile, outputFile };
		return args;
	}

	@Override
	public Boolean createReturn() throws EulerException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Consistency check failed.");
			throw new EulerException("Consistency check failed.");
		}
		
		File file = new File(outputFile);
		String output = "";
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
			String line = "";
			while((line = reader.readLine()) != null) {
				output += line + "\n";
			}
			//if(output.contains("...something"))
			//	return false;
		} catch (IOException e) {
			log(LogLevel.ERROR, "Format Check: Could not read output.", e);
			throw new EulerException("Format Check: Could not read output.");
		}		
		return true;
	}

}

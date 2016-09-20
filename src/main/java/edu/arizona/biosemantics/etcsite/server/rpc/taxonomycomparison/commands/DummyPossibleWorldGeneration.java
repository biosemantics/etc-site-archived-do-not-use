package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.io.File;

import org.apache.commons.io.FileUtils;

import edu.arizona.biosemantics.etcsite.server.Configuration;

public class DummyPossibleWorldGeneration implements PossibleWorldsGeneration {

	private String eulerInputFile;
	private String outputDir;

	public DummyPossibleWorldGeneration(String eulerInputFile, String outputDir) {
		this.eulerInputFile = eulerInputFile;
		this.outputDir = outputDir;
	}

	@Override
	public Void call() throws Exception {
		File pwDir = new File(outputDir + File.separator + "6-PWs-pdf");
		File aggregateDir = new File(outputDir + File.separator + "7-PWs-aggregate");
		File diagnosisDir = new File(outputDir + File.separator + "XYZ-diagnosis");
		pwDir.mkdirs();
		aggregateDir.mkdirs();
		diagnosisDir.mkdirs();
		FileUtils.copyDirectory(new File(Configuration.etcFiles + File.separator + "eulerdummyPWs"), pwDir);
		FileUtils.copyDirectory(new File(Configuration.etcFiles + File.separator + "eulerdummyAggregate"), aggregateDir);
		FileUtils.copyDirectory(new File(Configuration.etcFiles + File.separator + "eulerdummyDiagnosis"), diagnosisDir);
		
		try {
		    Thread.sleep(10000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		return null;
	}

	@Override
	public boolean isExecutedSuccessfully() {
		return true;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}

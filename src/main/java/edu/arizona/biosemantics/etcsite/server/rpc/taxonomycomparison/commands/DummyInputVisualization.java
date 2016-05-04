package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.io.File;

import org.apache.commons.io.FileUtils;

import edu.arizona.biosemantics.etcsite.server.Configuration;

public class DummyInputVisualization implements InputVisualization {

	private String eulerInputFile;
	private String outputDir;

	public DummyInputVisualization(String eulerInputFile, String outputDir) {
		this.eulerInputFile = eulerInputFile;
		this.outputDir = outputDir;
	}

	@Override
	public Void call() throws Exception {
		File inputDir = new File(outputDir + File.separator + "0-input");
		inputDir.mkdirs();
		FileUtils.copyDirectory(new File(Configuration.etcFiles + File.separator + "eulerdummyInputVisualization"), inputDir);
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

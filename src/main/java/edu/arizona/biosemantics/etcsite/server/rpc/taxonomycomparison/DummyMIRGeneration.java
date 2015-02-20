package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

public class DummyMIRGeneration implements MIRGeneration {

	private String eulerInputFile;
	private String outputDir;

	public DummyMIRGeneration(String eulerInputFile, String outputDir) {
		this.eulerInputFile = eulerInputFile;
		this.outputDir = outputDir;
	}

	@Override
	public Void call() throws Exception {
		File pwDir = new File(outputDir + File.separator + "6-PWs-pdf");
		pwDir.mkdirs();
		FileUtils.copyDirectory(new File("C:/Users/rodenhausen/etcsite/eulerdummyPWs"), pwDir);
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

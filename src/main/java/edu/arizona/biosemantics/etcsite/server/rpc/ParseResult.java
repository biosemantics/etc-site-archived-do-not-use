package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;
import java.util.Collection;

public class ParseResult {

	private Collection<File> outputFiles;
	
	public ParseResult(Collection<File> outputFiles) {
		super();
		this.outputFiles = outputFiles;
	}

	public Collection<File> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(Collection<File> outputFiles) {
		this.outputFiles = outputFiles;
	}
	
}

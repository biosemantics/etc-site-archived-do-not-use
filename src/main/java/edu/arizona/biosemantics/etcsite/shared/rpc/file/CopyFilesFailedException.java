package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class CopyFilesFailedException extends Exception {

	private String message = "";

	public CopyFilesFailedException() { }
	
	public CopyFilesFailedException(String message) {
		this.message = message;
	}

}

package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class FileDeleteFailedException extends Exception {

	public FileDeleteFailedException() { }
	
	public FileDeleteFailedException(String message) {
		super(message);
	}

}

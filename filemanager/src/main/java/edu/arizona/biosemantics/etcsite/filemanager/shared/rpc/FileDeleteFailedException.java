package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

public class FileDeleteFailedException extends Exception {

	public FileDeleteFailedException() { }
	
	public FileDeleteFailedException(String message) {
		super(message);
	}

}

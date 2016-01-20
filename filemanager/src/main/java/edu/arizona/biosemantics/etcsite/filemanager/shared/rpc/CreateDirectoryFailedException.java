package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

public class CreateDirectoryFailedException extends Exception {

	public CreateDirectoryFailedException() { }
	
	public CreateDirectoryFailedException(String message) {
		super(message);
	}
}

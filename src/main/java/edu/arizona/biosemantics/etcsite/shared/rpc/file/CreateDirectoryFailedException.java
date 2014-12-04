package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class CreateDirectoryFailedException extends Exception {

	public CreateDirectoryFailedException() { }
	
	public CreateDirectoryFailedException(String message) {
		super(message);
	}
}

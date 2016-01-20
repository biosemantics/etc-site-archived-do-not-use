package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

public class MoveFileFailedException extends Exception {

	public MoveFileFailedException() { }
	
	public MoveFileFailedException(String message) {
		super(message);
	}

}

package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class MoveFileFailedException extends Exception {

	private String message = "";
	
	public MoveFileFailedException() { }

	public MoveFileFailedException(String message) {
		this.message = message;
	}

}

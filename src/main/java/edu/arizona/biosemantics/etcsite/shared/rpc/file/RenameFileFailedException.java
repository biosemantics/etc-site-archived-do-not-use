package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class RenameFileFailedException extends Exception {

	private String message = "";

	public RenameFileFailedException(String message) {
		this.message = message;
	}

	public RenameFileFailedException() {
		// TODO Auto-generated constructor stub
	}

}

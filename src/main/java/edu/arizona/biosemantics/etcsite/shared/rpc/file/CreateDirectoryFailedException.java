package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class CreateDirectoryFailedException extends Exception {

	private String message = "";

	public CreateDirectoryFailedException(String message) {
		this.message = message;
	}

	public CreateDirectoryFailedException() {
		// TODO Auto-generated constructor stub
	}

}

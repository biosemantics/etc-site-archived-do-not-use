package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class FileDeleteFailedException extends Exception {

	private String message;

	public FileDeleteFailedException(String message) {
		this.message = message;
	}

	public FileDeleteFailedException() {
		// TODO Auto-generated constructor stub
	}

}

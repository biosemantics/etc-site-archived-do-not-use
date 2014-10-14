package edu.arizona.biosemantics.etcsite.shared.rpc.file;

public class ZipDirectoryFailedException extends Exception {

	private String message;

	public ZipDirectoryFailedException(String message) {
		this.message = message;
	}

	public ZipDirectoryFailedException() {
		// TODO Auto-generated constructor stub
	}

}

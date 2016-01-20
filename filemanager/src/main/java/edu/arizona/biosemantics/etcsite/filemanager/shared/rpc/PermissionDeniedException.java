package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

public class PermissionDeniedException extends Exception {

	public PermissionDeniedException() { }
	
	public PermissionDeniedException(String message) {
		super(message);
	}
	
}

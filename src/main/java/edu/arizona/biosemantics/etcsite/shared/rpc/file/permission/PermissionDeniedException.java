package edu.arizona.biosemantics.etcsite.shared.rpc.file.permission;

public class PermissionDeniedException extends Exception {

	public PermissionDeniedException() { }
	
	public PermissionDeniedException(String message) {
		super(message);
	}
	
}

package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;


public interface IContentValidator {
	
	public boolean validate(String input);

	public String getInvalidMessage();

}

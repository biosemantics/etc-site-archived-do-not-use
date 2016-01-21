package edu.arizona.biosemantics.etcsite.filemanager.server.process.validate;


public interface IContentValidator {
	
	public boolean validate(String input);

	public String getInvalidMessage();

}

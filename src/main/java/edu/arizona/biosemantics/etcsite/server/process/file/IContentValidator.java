package edu.arizona.biosemantics.etcsite.server.process.file;


public interface IContentValidator {
	
	public boolean validate(String input);

	public String getInvalidMessage();

}

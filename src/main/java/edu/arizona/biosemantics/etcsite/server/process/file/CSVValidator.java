package edu.arizona.biosemantics.etcsite.server.process.file;

public class CSVValidator implements IContentValidator {

	@Override
	public boolean validate(String input) {
		return true;
	}

	@Override
	public String getInvalidMessage() {
		return "";
	}
	
}

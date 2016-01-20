package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;

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

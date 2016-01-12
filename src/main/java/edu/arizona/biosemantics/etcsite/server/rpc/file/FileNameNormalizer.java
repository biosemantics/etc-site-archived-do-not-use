package edu.arizona.biosemantics.etcsite.server.rpc.file;

public class FileNameNormalizer {

	
	public String normalize(String name) {
		//replace all non-(word characters, dots, hyphens, whitespaces) by empty string
		String notAllowedFileNameCharacters = "[^\\w\\.\\-\\s]";
		name = name.replaceAll(notAllowedFileNameCharacters, "");
		//remove multiple whitespace
		name = name.replaceAll("\\s+", "\\s");
		return name;
	}
	
}

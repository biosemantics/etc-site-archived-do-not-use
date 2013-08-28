package edu.arizona.sirls.etc.site.shared.rpc;

public class FileFormatter {

	public String format(String input, FileType fileType) {
		switch(fileType) { 
		case TAXON_DESCRIPTION:
			return new XMLFileFormatter().format(input);
		case GLOSSARY:
			return new CSVFileFormatter().format(input);
		case EULER:
			return new XMLFileFormatter().format(input);
		}
		return input;
	}
	
}

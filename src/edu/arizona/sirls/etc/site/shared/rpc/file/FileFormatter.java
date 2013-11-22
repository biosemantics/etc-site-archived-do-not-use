package edu.arizona.sirls.etc.site.shared.rpc.file;

public class FileFormatter {

	public String format(String input, FileTypeEnum fileType) {
		switch(fileType) { 
		case TAXON_DESCRIPTION:
			return new XMLFileFormatter().format(input);
		case MARKED_UP_TAXON_DESCRIPTION:
			return new XMLFileFormatter().format(input);
		case PLAIN_TEXT:
			return input;
		case MATRIX:
			return new CSVFileFormatter().format(input);
		}
		return input;
	}
	
}

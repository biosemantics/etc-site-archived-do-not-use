package edu.arizona.biosemantics.etcsite.server.process.file;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class FileFormatter {

	public String format(String input, FileTypeEnum fileType) {
		switch(fileType) { 
		case TAXON_DESCRIPTION:
			return input;
			//return new XMLFileFormatter().format(input);
		case MARKED_UP_TAXON_DESCRIPTION:
			return input;
			//return new XMLFileFormatter().format(input);
		case PLAIN_TEXT:
			return input;
		case MATRIX:
			return input;
			//return new CSVFileFormatter().format(input);
		}
		return input;
	}
	
}

package edu.arizona.biosemantics.etcsite.shared.file;

import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatter;

public class CSVFileFormatter implements IFileFormatter {

	CSVValidator validator = new CSVValidator();
	String valueSeperator = ";";
	String dataRowSeperator = "\n";
	
	
	@Override
	public String format(String input) {
		if(!validator.validate(input)) 
			return input;
		StringBuilder result = new StringBuilder();
		String[] dataRows = input.split(dataRowSeperator);
		String[] values = input.split(valueSeperator);
		
		for(String dataRow : dataRows) {
			for(int i=0; i<values.length; i++) {
				String value = values[i];
				value = value.trim();
				result.append(value);
				if(i < values.length - 1)
					result.append(";");
			}
			result.append("\n");
		}
		
		return result.toString();
	}

}

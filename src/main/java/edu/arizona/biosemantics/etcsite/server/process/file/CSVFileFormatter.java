package edu.arizona.biosemantics.etcsite.server.process.file;


public class CSVFileFormatter implements IFileFormatter {

	private CSVValidator validator = new CSVValidator();
	private String valueSeperator = ";";
	private String dataRowSeperator = "\n";
	
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

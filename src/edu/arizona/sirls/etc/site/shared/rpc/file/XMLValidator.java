package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XMLValidator implements IContentValidator {

	private File schemaFile;

	public XMLValidator(File schemaFile) {
		this.schemaFile = schemaFile;
	}
	
	@Override
	public boolean validate(String input) {
		try {			
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(schemaSource);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(input)));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}

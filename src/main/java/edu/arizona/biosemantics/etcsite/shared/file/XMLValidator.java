package edu.arizona.biosemantics.etcsite.shared.file;

import java.io.ByteArrayInputStream;
import java.io.File;

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
			validator.validate(new StreamSource(new ByteArrayInputStream(input.getBytes("UTF-8"))));
			return true;
		} catch (Exception e) {
			//System.out.println(schemaFile.getAbsolutePath());
			//System.out.println(input);
			//e.printStackTrace();
			return false;
		}
	}
	
}

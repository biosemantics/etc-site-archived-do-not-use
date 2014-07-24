package edu.arizona.biosemantics.etcsite.shared.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XMLValidator implements IContentValidator {

	private File schemaFile;
	private URL url;

	public XMLValidator(File schemaFile) {
		this.schemaFile = schemaFile;
	}
	
	public XMLValidator(URL url) {
		this.url = url;
	}
	
	@Override
	public boolean validate(String input) {
		try {			
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaSource = null;
			if(schemaFile != null) {
				schemaSource = new StreamSource(schemaFile);
			} else if(url != null) {
			    InputStream inputStream = url.openStream();
			    schemaSource = new StreamSource(inputStream);
			}
			if(schemaSource != null) {
				Schema schema = factory.newSchema(schemaSource);
				Validator validator = schema.newValidator();	
				validator.validate(new StreamSource(new ByteArrayInputStream(input.getBytes("UTF-8"))));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}

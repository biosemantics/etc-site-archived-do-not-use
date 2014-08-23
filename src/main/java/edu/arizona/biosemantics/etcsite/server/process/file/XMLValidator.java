package edu.arizona.biosemantics.etcsite.server.process.file;

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
			Source schemaSource = null;
			if(schemaFile != null) {
				schemaSource = new StreamSource(schemaFile);
				return validate(input, schemaSource);
			} else if(url != null) {
				try(InputStream inputStream = url.openStream()) {
				    schemaSource = new StreamSource(inputStream);
				    return validate(input, schemaSource);
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean validate(String input, Source schemaSource) throws Exception {
		if(schemaSource != null) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(schemaSource);
			Validator validator = schema.newValidator();	
			try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes("UTF-8"))) {
				validator.validate(new StreamSource(byteArrayInputStream));
			}
			return true;
		} else {
			return false;
		}
	}
	
}

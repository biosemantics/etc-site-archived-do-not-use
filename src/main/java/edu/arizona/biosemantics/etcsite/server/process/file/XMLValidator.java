package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;

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
		Source schemaSource = null;
		if(schemaFile != null) {
			schemaSource = new StreamSource(schemaFile);
			return validate(input, schemaSource);
		} else if(url != null) {
			try(InputStream inputStream = url.openStream()) {
			    schemaSource = new StreamSource(inputStream);
			    return validate(input, schemaSource);
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't open or close input stream from url", e);
			}
		}
		return false;
	}

	private boolean validate(String input, Source schemaSource) {
		if(schemaSource != null) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = null;
			try {
				schema = factory.newSchema(schemaSource);
			} catch (SAXException e) {
				log(LogLevel.ERROR, "Couldn't create schema", e);
			}
			if(schema != null) {
				Validator validator = schema.newValidator();	
				try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes("UTF-8"))) {
					try {
						validator.validate(new StreamSource(byteArrayInputStream));
					} catch (IOException e) {
						log(LogLevel.ERROR, "Couldn't validate xml document", e);
					} catch(SAXException e) {
						return false;
					}
					return true;
				} catch (UnsupportedEncodingException e) {
					log(LogLevel.ERROR, "Encoding not supported", e);
				} catch (IOException e) {
					log(LogLevel.ERROR, "Couldn't open or close byte array strema", e);
				}
			}
		}
		return false;
	}
	
}

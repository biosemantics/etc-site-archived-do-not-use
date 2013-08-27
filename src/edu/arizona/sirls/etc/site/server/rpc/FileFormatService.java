package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileService fileService = new FileService();
	private String taxonDescriptionSchemaFile = "C://test//schema.xml";	
	
	@Override
	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileService.getFileContent(authenticationToken, target);
			Source xmlFile = new StreamSource(new StringReader(fileContent)); 
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			try {
				Schema schema = schemaFactory.newSchema(new File(taxonDescriptionSchemaFile));
				Validator validator = schema.newValidator();
				validator.validate(xmlFile);
				return true;
			} catch(Exception e) { 
				e.printStackTrace();
				return false;
			}
		} 
		return false;
	}

	@Override
	public boolean isValidGlossary(AuthenticationToken authenticationToken,
			String target) {
		return target.endsWith(".csv");
	}

}

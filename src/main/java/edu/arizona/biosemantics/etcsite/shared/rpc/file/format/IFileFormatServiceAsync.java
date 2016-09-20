package edu.arizona.biosemantics.etcsite.shared.rpc.file.format;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;


public interface IFileFormatServiceAsync {
	
	public void isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent, AsyncCallback<Boolean> callback);
	
	public void isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content, AsyncCallback<Boolean> callback);
	
	public void isValidMatrix(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);

	public void isValidGlossary(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void isValidEuler(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text, 
			AsyncCallback<List<XmlModelFile>> callback);
	
	public void getSchema(AuthenticationToken token, String filePath, AsyncCallback<String> callback);
	
	public void isValidXmlContentForSchema(AuthenticationToken token, String text, String currentXmlSchema, AsyncCallback<Boolean> callback);
	
	public void setSchema(AuthenticationToken token, String content, FileTypeEnum fileTypeEnum, AsyncCallback<String> callback);
	
}

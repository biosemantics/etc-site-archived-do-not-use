package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFile;


public interface IFileFormatServiceAsync {
	
	public void isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	public void isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent, AsyncCallback<RPCResult<Boolean>> callback);
	public void isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidMatrix(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);

	public void isValidGlossary(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidEuler(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text, 
			AsyncCallback<RPCResult<List<XmlModelFile>>> callback);
	
}

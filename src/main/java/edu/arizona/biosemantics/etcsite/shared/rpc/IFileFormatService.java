package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFile;


@RemoteServiceRelativePath("fileFormat")
public interface IFileFormatService extends RemoteService {

	public RPCResult<Boolean> isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent);	

	public RPCResult<Boolean> isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidMatrix(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidGlossary(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Boolean> isValidEuler(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Boolean> isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content);
	
	public RPCResult<List<XmlModelFile>> createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text);
	
	public RPCResult<String> getSchema(AuthenticationToken token, String filePath);
	
	public RPCResult<Boolean> isValidXmlContentForSchema(AuthenticationToken token, String text, String currentXmlSchema);
	
	public RPCResult<String> setSchema(AuthenticationToken token, String content, FileTypeEnum fileTypeEnum);
}

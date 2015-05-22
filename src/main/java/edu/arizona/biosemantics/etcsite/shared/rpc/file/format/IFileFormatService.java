package edu.arizona.biosemantics.etcsite.shared.rpc.file.format;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;


@RemoteServiceRelativePath("fileFormat")
public interface IFileFormatService extends RemoteService {

	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath)
			throws PermissionDeniedException, GetFileContentFailedException;
	
	public boolean isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent);	

	public boolean isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException;
	
	public boolean isValidMatrix(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException;
	
	public boolean isValidGlossary(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException;

	public boolean isValidEuler(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException;

	public boolean isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content);
	
	public List<XmlModelFile> createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text);
	
	public String getSchema(AuthenticationToken token, String filePath) throws PermissionDeniedException, GetFileContentFailedException;
	
	public boolean isValidXmlContentForSchema(AuthenticationToken token, String text, String currentXmlSchema);
	
	public String setSchema(AuthenticationToken token, String content, FileTypeEnum fileTypeEnum);
}

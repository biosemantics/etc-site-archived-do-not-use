package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("fileAccess")
public interface IFileAccessService extends RemoteService {

	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content) 
		throws SetFileContentFailedException, PermissionDeniedException;
	
	public String getFileContent(AuthenticationToken authenticationToken, String filePath)
		throws PermissionDeniedException, GetFileContentFailedException;
	
	public String getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType)
		throws PermissionDeniedException, GetFileContentFailedException;
	
	public String getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType)
		throws PermissionDeniedException, GetFileContentFailedException;
	
}

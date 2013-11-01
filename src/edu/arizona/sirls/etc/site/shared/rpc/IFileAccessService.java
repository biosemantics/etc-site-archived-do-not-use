package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

@RemoteServiceRelativePath("fileAccess")
public interface IFileAccessService extends RemoteService {

	public RPCResult<Void> setFileContent(AuthenticationToken authenticationToken, String filePath, String content);
	
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<String> getFileContent(AuthenticationToken authenticationToken, String filePath, FileType fileType);
	
	public RPCResult<String> getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileType fileType);
	
}

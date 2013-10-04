package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

@RemoteServiceRelativePath("fileAccess")
public interface IFileAccessService extends RemoteService {

	public boolean setFileContent(AuthenticationToken authenticationToken, String target, String content);
	
	public String getFileContent(AuthenticationToken authenticationToken, String target);
	
	public String getFileContent(AuthenticationToken authenticationToken, String target, FileType fileType);
	
}
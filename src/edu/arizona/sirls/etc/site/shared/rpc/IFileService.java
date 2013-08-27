package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

@RemoteServiceRelativePath("file")
public interface IFileService extends RemoteService {

	public Tree<String> getUsersFiles(AuthenticationToken authenticationToken); 
	
	public boolean deleteFile(AuthenticationToken authenticationToken, String target);

	public boolean moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget);

	public boolean createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName);

	public boolean setFileContent(AuthenticationToken authenticationToken, String target, String content);
	
	public String getFileContent(AuthenticationToken authenticationToken, String target);
	
}

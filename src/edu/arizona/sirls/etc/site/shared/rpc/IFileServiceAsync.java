package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

public interface IFileServiceAsync {

	public void getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter, AsyncCallback<Tree<String>> callback);

	public void deleteFile(AuthenticationToken authenticationToken, String target,
			AsyncCallback<Boolean> callback);
	
	public void moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget, AsyncCallback<Boolean> callback);

	public void createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName, AsyncCallback<Boolean> callback);
	

	
}

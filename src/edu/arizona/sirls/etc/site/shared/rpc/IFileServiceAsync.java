package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.api.file.DeleteFileAsyncCallback;


public interface IFileServiceAsync {

	public void getUsersFiles(AuthenticationToken authenticationToken, AsyncCallback<Tree<String>> callback);

	public void deleteFile(AuthenticationToken authenticationToken, String target,
			AsyncCallback<Boolean> callback);
	
	public void moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget, AsyncCallback<Boolean> callback);

	public void createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName, AsyncCallback<Boolean> callback);
	
	public void setFileContent(AuthenticationToken authenticationToken, String target, String content, AsyncCallback<Boolean> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String target, AsyncCallback<String> callback);
	
}

package edu.arizona.biosemantics.etcsite.shared.rpc.file.permission;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.file.FilePermissionType;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IFilePermissionServiceAsync {

	public void hasReadPermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void hasWritePermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void getPermissionType(AuthenticationToken authenticationToken, String filePath, AsyncCallback<FilePermissionType> callback);
	
	public void isSharedFilePath(int userId, String filePath, AsyncCallback<Boolean> callback);

	public void isOwnedFilePath(int userId, String filePath, AsyncCallback<Boolean> callback);

	public void isPublicFilePath(String filePath, AsyncCallback<Boolean> callback);
	
}

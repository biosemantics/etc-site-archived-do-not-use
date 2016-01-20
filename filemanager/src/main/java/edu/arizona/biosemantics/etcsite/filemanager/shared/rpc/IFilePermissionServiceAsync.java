package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.common.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FilePermissionType;

public interface IFilePermissionServiceAsync {

	public void hasReadPermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void hasWritePermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void getPermissionType(AuthenticationToken authenticationToken, String filePath, AsyncCallback<FilePermissionType> callback);
	
	public void isSharedFilePath(int userId, String filePath, AsyncCallback<Boolean> callback);

	public void isOwnedFilePath(int userId, String filePath, AsyncCallback<Boolean> callback);
	
}

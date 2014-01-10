package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.file.FilePermissionType;

public interface IFilePermissionServiceAsync {

	public void hasReadPermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void hasWritePermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void getPermissionType(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<FilePermissionType>> callback);
	
	public void isSharedFilePath(String username, String filePath, AsyncCallback<RPCResult<Boolean>> callback);

	public void isOwnedFilePath(String username, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
}

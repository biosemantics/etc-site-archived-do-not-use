package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePermissionType;

public interface IFilePermissionServiceAsync {

	public void hasReadPermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void hasWritePermission(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void getPermissionType(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<FilePermissionType>> callback);
	
}

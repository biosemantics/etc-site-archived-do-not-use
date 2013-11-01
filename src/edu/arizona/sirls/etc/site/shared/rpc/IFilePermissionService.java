package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePermissionType;

@RemoteServiceRelativePath("filePermission")
public interface IFilePermissionService extends RemoteService {

	public RPCResult<Boolean> hasReadPermission(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> hasWritePermission(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<FilePermissionType> getPermissionType(AuthenticationToken authenticationToken, String filePath);
}

package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.FilePermissionType;

@RemoteServiceRelativePath("filePermission")
public interface IFilePermissionService extends RemoteService {

	public RPCResult<Boolean> hasReadPermission(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> hasWritePermission(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<FilePermissionType> getPermissionType(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isSharedFilePath(int userId, String filePath);

	public RPCResult<Boolean> isOwnedFilePath(int userId, String filePath);
}

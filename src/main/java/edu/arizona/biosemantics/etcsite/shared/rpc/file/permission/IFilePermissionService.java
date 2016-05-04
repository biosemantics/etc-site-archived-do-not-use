package edu.arizona.biosemantics.etcsite.shared.rpc.file.permission;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.file.FilePermissionType;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("filePermission")
public interface IFilePermissionService extends RemoteService {

	public boolean hasReadPermission(AuthenticationToken authenticationToken, String filePath);
	
	public boolean hasWritePermission(AuthenticationToken authenticationToken, String filePath);
	
	public FilePermissionType getPermissionType(AuthenticationToken authenticationToken, String filePath);
	
	public boolean isSharedFilePath(int userId, String filePath);

	public boolean isOwnedFilePath(int userId, String filePath);

	public boolean isPublicFilePath(String filePath);
}

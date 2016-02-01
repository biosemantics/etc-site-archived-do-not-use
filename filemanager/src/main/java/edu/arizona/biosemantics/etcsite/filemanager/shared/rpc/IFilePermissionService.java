package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FilePermissionType;

@RemoteServiceRelativePath("filePermission")
public interface IFilePermissionService extends RemoteService {

	public boolean hasReadPermission(AuthenticationToken authenticationToken, String filePath);
	
	public boolean hasWritePermission(AuthenticationToken authenticationToken, String filePath);
	
	public FilePermissionType getPermissionType(AuthenticationToken authenticationToken, String filePath);
	
	public boolean isSharedFilePath(int userId, String filePath);
	
	public boolean isPublicFilePath(String filePath);

	public boolean isOwnedFilePath(int userId, String filePath);
}

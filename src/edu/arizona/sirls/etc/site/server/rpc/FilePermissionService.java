package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IFilePermissionService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePermissionType;

public class FilePermissionService extends RemoteServiceServlet implements IFilePermissionService {

	private static final long serialVersionUID = 7670782397695216737L;

	@Override
	public RPCResult<Boolean> hasReadPermission(AuthenticationToken authenticationToken, String filePath) {
		
		
		return null;
	}

	@Override
	public RPCResult<FilePermissionType> getPermissionType(AuthenticationToken authenticationToken, String filePath) {
		
		return null;
	}

	@Override
	public RPCResult<Boolean> hasWritePermission(AuthenticationToken authenticationToken, String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

}

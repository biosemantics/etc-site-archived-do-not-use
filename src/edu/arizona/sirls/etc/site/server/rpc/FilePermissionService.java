package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFilePermissionService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.AbstractTaskConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShareDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskConfigurationDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePermissionType;

public class FilePermissionService extends RemoteServiceServlet implements IFilePermissionService {

	private static final long serialVersionUID = 7670782397695216737L;
	private IAuthenticationService authenticationService = new AuthenticationService();

	@Override
	public RPCResult<Boolean> hasReadPermission(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<Boolean>(true, true);
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		
		if(isOwnedFilePath(authenticationToken.getUsername(), filePath))
			return new RPCResult<Boolean>(true, true);
		try {
			if(isSharedFilePath(authenticationToken.getUsername(), filePath)) 
				return new RPCResult<Boolean>(true, true);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Boolean>(false, "Couldn't determine sharing");
		}
		return new RPCResult<Boolean>(true, false);
	}

	@Override
	public RPCResult<FilePermissionType> getPermissionType(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<FilePermissionType>(true, FilePermissionType.ADMIN);
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<FilePermissionType>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<FilePermissionType>(false, "Authentication failed");
		
		if(isOwnedFilePath(authenticationToken.getUsername(), filePath))
			return new RPCResult<FilePermissionType>(true, FilePermissionType.OWNER);
		try {
			if(isSharedFilePath(authenticationToken.getUsername(), filePath)) 
				return new RPCResult<FilePermissionType>(true, FilePermissionType.SHARED_WITH);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<FilePermissionType>(false, "Couldn't determine sharing");
		}
		return new RPCResult<FilePermissionType>(true, FilePermissionType.NONE);
	}

	@Override
	public RPCResult<Boolean> hasWritePermission(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<Boolean>(true, true);
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		
		if(isOwnedFilePath(authenticationToken.getUsername(), filePath))
			return new RPCResult<Boolean>(true, true);
		return new RPCResult<Boolean>(true, true);
	}
	
	private boolean isSharedFilePath(String username, String filePath) throws ClassNotFoundException, SQLException, IOException {
		ShortUser user = UserDAO.getInstance().getShortUser(username);
		List<Share> invitedShares = ShareDAO.getInstance().getSharesOfInvitee(user);
		for(Share share : invitedShares) {
			AbstractTaskConfiguration taskConfiguration = TaskConfigurationDAO.getInstance().getTaskConfiguration(share.getTask().getConfiguration());
			for(String input : taskConfiguration.getInputs())
				if(containedInPath(input, filePath))
					return true;
			for(String output : taskConfiguration.getOutputs())
				if(containedInPath(output, filePath))
					return true;
		}
		return false;
	}

	private boolean containedInPath(String possibleParent, String filePath) {
		possibleParent = normalizePath(possibleParent);
		filePath = normalizePath(filePath);
		if(filePath.startsWith(possibleParent)) 
			return true;
		return false;
	}

	private String normalizePath(String filePath) {
		return filePath;
	}
	
	private boolean isOwnedFilePath(String username, String filePath) {
		String ownedFilesDirectory = Configuration.fileBase + File.separator + username;
		return containedInPath(ownedFilesDirectory, filePath);
	}

}

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
		
		RPCResult<Boolean> ownedResult = isOwnedFilePath(authenticationToken.getUsername(), filePath);
		if(ownedResult.isSucceeded() && ownedResult.getData())
			return new RPCResult<Boolean>(true, true);
		try {
			RPCResult<Boolean> sharedResult = isSharedFilePath(authenticationToken.getUsername(), filePath);
			if(sharedResult.isSucceeded() && sharedResult.getData()) 
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
		
		RPCResult<Boolean> ownedResult = isOwnedFilePath(authenticationToken.getUsername(), filePath);
		if(ownedResult.isSucceeded() && ownedResult.getData())
			return new RPCResult<FilePermissionType>(true, FilePermissionType.OWNER);
		try {
			RPCResult<Boolean> sharedResult = isSharedFilePath(authenticationToken.getUsername(), filePath);
			if(sharedResult.isSucceeded() && sharedResult.getData()) 
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
		
		RPCResult<Boolean> ownedResult = isOwnedFilePath(authenticationToken.getUsername(), filePath);
		if(ownedResult.isSucceeded() && ownedResult.getData())
			return new RPCResult<Boolean>(true, true);
		return new RPCResult<Boolean>(true, true);
	}
	
	@Override
	public RPCResult<Boolean> isSharedFilePath(String username, String filePath) {
		if(filePath.startsWith("Share.") || filePath.equals("Shared"))
			return new RPCResult<Boolean>(true, true);
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(username);
			List<Share> invitedShares = ShareDAO.getInstance().getSharesOfInvitee(user);
			for(Share share : invitedShares) {
				AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
				for(String input : taskConfiguration.getInputs())
					if(containedInPath(input, filePath))
						return new RPCResult<Boolean>(true, true);
				for(String output : taskConfiguration.getOutputs())
					if(containedInPath(output, filePath))
						return new RPCResult<Boolean>(true, true);
			}
			return new RPCResult<Boolean>(true, false);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult(false, "Internal Server Error");
		}
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
	
	@Override
	public RPCResult<Boolean> isOwnedFilePath(String username, String filePath) {
		if(filePath.equals("Owned") || filePath.equals("Root"))
			return new RPCResult<Boolean>(true, true);
		String ownedFilesDirectory = Configuration.fileBase + File.separator + username;
		return new RPCResult<Boolean>(true, containedInPath(ownedFilesDirectory, filePath));
	}

}

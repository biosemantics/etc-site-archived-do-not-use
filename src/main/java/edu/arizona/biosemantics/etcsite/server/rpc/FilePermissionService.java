package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.db.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Share;
import edu.arizona.biosemantics.etcsite.shared.db.ShareDAO;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.file.FilePermissionType;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;


public class FilePermissionService extends RemoteServiceServlet implements IFilePermissionService {

	private static final long serialVersionUID = 7670782397695216737L;

	@Override
	public RPCResult<Boolean> hasReadPermission(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<Boolean>(true, true);
		
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

package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;

import java.io.File;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.core.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.core.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.core.shared.model.Share;
import edu.arizona.biosemantics.etcsite.core.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.server.Configuration;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FilePermissionType;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFilePermissionService;

public class FilePermissionService extends RemoteServiceServlet implements IFilePermissionService {

	private static final long serialVersionUID = 7670782397695216737L;

	private DAOManager daoManager;
	
	@Inject
	public FilePermissionService(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public boolean hasReadPermission(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return true;
		boolean ownedResult = isOwnedFilePath(authenticationToken.getUserId(), filePath);
		if(ownedResult)
			return true;
		boolean sharedResult = isSharedFilePath(authenticationToken.getUserId(), filePath);
		if(sharedResult) 
			return true;
		boolean publicResult = isPublicFilePath(filePath);
		if(publicResult)
			return true;
		return false;
	}

	private boolean isPublicFilePath(String filePath) {
		return filePath.startsWith(Configuration.publicFolder);
	}

	@Override
	public FilePermissionType getPermissionType(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return FilePermissionType.ADMIN;
		if(isOwnedFilePath(authenticationToken.getUserId(), filePath))
			return FilePermissionType.OWNER;
		if(isSharedFilePath(authenticationToken.getUserId(), filePath))
			return FilePermissionType.SHARED_WITH;
		return FilePermissionType.NONE;
	}

	@Override
	public boolean hasWritePermission(AuthenticationToken authenticationToken, String filePath) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return true;
		
		return isOwnedFilePath(authenticationToken.getUserId(), filePath);
	}
	
	@Override
	public boolean isSharedFilePath(int userId, String filePath) {
		if(filePath.startsWith("Share.") || filePath.equals("Shared"))
			return true;
		ShortUser user = daoManager.getUserDAO().getShortUser(userId);
		List<Share> invitedShares = daoManager.getShareDAO().getSharesOfInvitee(user);
		for(Share share : invitedShares) {
			AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
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
	
	@Override
	public boolean isOwnedFilePath(int userId, String filePath) {
		if(filePath.equals("Owned") || filePath.equals("Root"))
			return true;
		String ownedFilesDirectory = edu.arizona.biosemantics.etcsite.core.server.Configuration.fileBase + File.separator + userId;
		String compressedOwnedFilesDirectory = Configuration.compressedFileBase + File.separator + userId;
		boolean result = containedInPath(ownedFilesDirectory, filePath) || containedInPath(compressedOwnedFilesDirectory, filePath);
		return result;
	}

}

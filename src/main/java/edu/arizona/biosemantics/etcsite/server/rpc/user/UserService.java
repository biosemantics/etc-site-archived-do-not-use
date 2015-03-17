package edu.arizona.biosemantics.etcsite.server.rpc.user;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.BCrypt;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.RegistrationFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidPasswordException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserAddException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class UserService extends RemoteServiceServlet implements IUserService {
	
	private DAOManager daoManager = new DAOManager();
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public List<ShortUser> getUsers(AuthenticationToken authenticationToken, boolean includeSelf) {
		List<ShortUser> usernames;
		if(includeSelf)
			usernames = daoManager.getUserDAO().getUsers();
		else
			usernames = daoManager.getUserDAO().getUsersWithout(authenticationToken.getUserId());
		return usernames;
	}

	@Override
	public ShortUser getUser(AuthenticationToken authenticationToken) throws UserNotFoundException {
		int userId = authenticationToken.getUserId();
		ShortUser user = daoManager.getUserDAO().getShortUser(userId);
		if(user != null){
			return user;
		} else { 
			throw new UserNotFoundException();
		}
	}
	
	@Override
	public ShortUser add(String firstName, String lastName, String email, String password) throws UserAddException {
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		if(!daoManager.getUserDAO().hasUser(email)) {
			User user = daoManager.getUserDAO().insert(new User(encryptedPassword, firstName, lastName, email, "", "", ""));
			if(user == null) {
				throw new UserAddException("Adding user failed");
			}
			return new ShortUser(user);
		} else 
			throw new UserAddException("Email already exists");
	}
	
	@Override
	public ShortUser add(String openIdProviderId, String string, String firstName,
			String lastName, String password) throws UserAddException {
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		if(daoManager.getUserDAO().hasUser(openIdProviderId)) {
			User user = daoManager.getUserDAO().insert(new User(openIdProviderId, "google", password, firstName, lastName, "", "", ""));
			if(user == null) {
				throw new UserAddException("Adding user failed");
			}
			return new ShortUser(user);
		} else 
			throw new UserAddException("Email already exists");
	}
	
	@Override
	public ShortUser update(AuthenticationToken authenticationToken, String oldPassword, String newPassword, 
			ShortUser shortUser) throws UserNotFoundException, InvalidPasswordException {
		User user = daoManager.getUserDAO().getUser(shortUser.getId());
		if(user == null)
			throw new UserNotFoundException();			
			
		if(!BCrypt.checkpw(oldPassword, user.getPassword())) {
			throw new InvalidPasswordException(); 
		}
		
		String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setAffiliation(shortUser.getAffiliation());
		user.setBioportalAPIKey(shortUser.getBioportalApiKey());
		user.setBioportalUserId(shortUser.getBioportalUserId());
		user.setEmail(shortUser.getEmail());
		user.setFirstName(shortUser.getFirstName());
		user.setLastName(shortUser.getLastName());
		user.setPassword(encryptedPassword);
		user.setMatrixGenerationEmailChk(shortUser.getMatrixGenerationEmailChk());
		user.setTextCaptureEmailChk(shortUser.getTextCaptureEmailChk());
		user.setTaxonomyComparisonEmailChk(shortUser.getTaxonomyComparisonEmailChk());
		user.setTreeGenerationEmailChk(shortUser.getTreeGenerationEmailChk());
		
		daoManager.getUserDAO().update(user);
		return daoManager.getUserDAO().getShortUser(shortUser.getId());
	}
	
	@Override
	public boolean existsUser(String email) {
		return daoManager.getUserDAO().hasUser(email);
	}
	
}

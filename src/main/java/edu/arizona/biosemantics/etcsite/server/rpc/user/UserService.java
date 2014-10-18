package edu.arizona.biosemantics.etcsite.server.rpc.user;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.BCrypt;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.RegistrationFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidPasswordException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;

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
	public void add(
			int captchaId, String captchaSolution, String firstName, String lastName, String email, String password) 
			throws CaptchaException, RegistrationFailedException {
		if (!daoManager.getCaptchaDAO().isValidSolution(captchaId, captchaSolution)){
			throw new CaptchaException();
		}
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		User emailUser = daoManager.getUserDAO().getUser(email);
		if(emailUser == null) {
			User user = daoManager.getUserDAO().insert(new User(encryptedPassword, firstName, lastName, email, "", "", ""));
			if(user == null) {
				throw new RegistrationFailedException("Registration Failed");
			}
			return;
		} else 
			throw new RegistrationFailedException("Email already exists");
	}
	
	@Override
	public void update(AuthenticationToken authenticationToken, String oldPassword, String newPassword, 
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
		
		daoManager.getUserDAO().update(user);
	}
	
}

package edu.arizona.biosemantics.etcsite.shared.rpc.user;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.RegistrationFailedException;

@RemoteServiceRelativePath("user")
public interface IUserService extends RemoteService {

	public List<ShortUser> getUsers(AuthenticationToken authenticationToken, boolean includeSelf);
	
	public ShortUser getUser(AuthenticationToken authenticationToken) throws UserNotFoundException;
	
	public ShortUser add(String firstName, 
			String lastName, String email, String password) throws UserAddException;
	
	public ShortUser update(
			AuthenticationToken authenticationToken, String oldPassword,String newPasswort,
			ShortUser user) throws UserNotFoundException, InvalidPasswordException;

	public boolean existsUser(String openIdProviderId);

	public ShortUser add(String openIdProviderId, String string, String firstName,
			String lastName, String encryptedDummyPassword) throws UserAddException;
	
}
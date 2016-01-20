package edu.arizona.biosemantics.etcsite.core.shared.rpc.user;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.core.shared.model.User.EmailPreference;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.Help;


@RemoteServiceRelativePath("user")
public interface IUserService extends RemoteService {

	public List<ShortUser> getUsers(AuthenticationToken authenticationToken, boolean includeSelf);
	
	public ShortUser getUser(AuthenticationToken authenticationToken) throws UserNotFoundException;
	
	public ShortUser add(String firstName, 
			String lastName, String email, String password) throws UserAddException;
	
	public ShortUser update(AuthenticationToken authenticationToken, ShortUser user) throws UserNotFoundException;
	
	public ShortUser update(AuthenticationToken authenticationToken, String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException;

	public boolean existsUser(String openIdProviderId);

	public ShortUser add(String openIdProviderId, String string, String firstName,
			String lastName, String password) throws UserAddException;
	
	public edu.arizona.biosemantics.oto.model.User createOTOAccount(AuthenticationToken authenticationToken, String email, String password) throws CreateOTOAccountException;
	
	public void saveOTOAccount(AuthenticationToken authenticationToken, boolean share, String email, String password) throws InvalidOTOAccountException, OTOException; 
	
	public edu.arizona.biosemantics.oto.model.User createOTOAccount(AuthenticationToken token, String googleCode) throws CreateOTOAccountException, OTOException;
	
	public edu.arizona.biosemantics.oto.model.User saveOTOAccount(AuthenticationToken token, String googleCode) throws UserNotFoundException, InvalidOTOAccountException, OTOException;
	
	public boolean isProfile(AuthenticationToken token, Help help);
	
	public void setProfile(AuthenticationToken token, Help help, boolean dontShowPopup);
	
	public boolean isProfile(AuthenticationToken token, EmailPreference emailPreferences);
	
	public void setProfile(AuthenticationToken token, EmailPreference emailPreferences, boolean dontShowPopup);
	
	public boolean hasLinkedOTOAccount(AuthenticationToken token);
}
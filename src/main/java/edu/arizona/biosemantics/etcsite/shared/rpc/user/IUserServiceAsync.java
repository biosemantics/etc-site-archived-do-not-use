package edu.arizona.biosemantics.etcsite.shared.rpc.user;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.model.User.EmailPreference;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;

public interface IUserServiceAsync {

	public void getUsers(AuthenticationToken authenticationToken, boolean includeSelf, AsyncCallback<List<ShortUser>> callback);
	
	public void getUser(AuthenticationToken authenticationToken, AsyncCallback<ShortUser> callback);
	
	public void add(String firstName, String lastName, String email, String password, AsyncCallback<ShortUser> callback);
		
	public void update(AuthenticationToken authenticationToken, String oldPassword,	String newPassword, AsyncCallback<ShortUser> callback);
	
	public void existsUser(String openIdProviderId, AsyncCallback<Boolean> callback);

	public void add(String openIdProviderId, String string, String firstName,
			String lastName, String password, AsyncCallback<ShortUser> callback);

	public void createOTOAccount(AuthenticationToken authenticationToken, String email, String password,
			AsyncCallback<edu.arizona.biosemantics.oto.model.User> asyncCallback);

	public void saveOTOAccount(AuthenticationToken authenticationToken, boolean share, String email, String password,
			AsyncCallback<Void> asyncCallback);

	public void createOTOAccount(AuthenticationToken token, String googleCode, AsyncCallback<edu.arizona.biosemantics.oto.model.User> callback);

	public void saveOTOAccount(AuthenticationToken token, String googleCode, AsyncCallback<edu.arizona.biosemantics.oto.model.User> callback);

	public void isProfile(AuthenticationToken token, Help help, AsyncCallback<Boolean> callback);
	
	public void setProfile(AuthenticationToken token, Help help, boolean dontShowPopup, AsyncCallback<Void> callback);
	
	public void isProfile(AuthenticationToken token, EmailPreference emailPreferences, AsyncCallback<Boolean> callback);
	
	public void setProfile(AuthenticationToken token, EmailPreference emailPreferences, boolean dontShowPopup, AsyncCallback<Void> callback);

	public void hasLinkedOTOAccount(AuthenticationToken token, AsyncCallback<Boolean> asyncCallback);

	public void updateBioportal(AuthenticationToken authenticationToken, String bioportalApiKey, String bioportalUserId,AsyncCallback<ShortUser> callback);

	public void updateEmailNotification(AuthenticationToken authenticationToken,Map<String, Boolean> emailPreferences, AsyncCallback<ShortUser> callback);

	void updateName(AuthenticationToken authenticationToken, String firstName, String lastName, String affiliation,AsyncCallback<ShortUser> callback);

}

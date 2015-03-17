package edu.arizona.biosemantics.etcsite.shared.rpc.user;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IUserServiceAsync {

	public void getUsers(AuthenticationToken authenticationToken, boolean includeSelf, AsyncCallback<List<ShortUser>> callback);
	
	public void getUser(AuthenticationToken authenticationToken, AsyncCallback<ShortUser> callback);
	
	public void add(String firstName, String lastName, String email, String password, AsyncCallback<ShortUser> callback);
	
	public void update(AuthenticationToken authenticationToken, ShortUser user, AsyncCallback<ShortUser> callback);
	
	public void update(AuthenticationToken authenticationToken, String oldPassword,	String newPassword, AsyncCallback<ShortUser> callback);
	
	public void existsUser(String openIdProviderId, AsyncCallback<Boolean> callback);

	public void add(String openIdProviderId, String string, String firstName,
			String lastName, String encryptedDummyPassword, AsyncCallback<ShortUser> callback);

	public void createOTOAccount(AuthenticationToken authenticationToken, String email, String password,
			AsyncCallback<edu.arizona.biosemantics.oto.common.model.User> asyncCallback);

	public void saveOTOAccount(AuthenticationToken authenticationToken, boolean share, String email, String password,
			AsyncCallback<Void> asyncCallback);

	public void createOTOAccount(AuthenticationToken token, String googleCode, AsyncCallback<edu.arizona.biosemantics.oto.common.model.User> asyncCallback);
}

package edu.arizona.biosemantics.etcsite.shared.rpc.user;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.RegistrationFailedException;

public interface IUserServiceAsync {

	public void getUsers(AuthenticationToken authenticationToken, boolean includeSelf, AsyncCallback<List<ShortUser>> callback);
	
	public void getUser(AuthenticationToken authenticationToken, AsyncCallback<ShortUser> callback);
	
	public void add(int captchaId, String captchaSolution, String firstName,
			String lastName, String email, String password, AsyncCallback<Void> callback);
	
	public void update(
			AuthenticationToken authenticationToken, String oldPassword,
			String newPassword, ShortUser user, AsyncCallback<Void> callback);
	
	
}

package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public interface IUserServiceAsync {

	public void getUsers(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<List<ShortUser>>> callback);
	
}

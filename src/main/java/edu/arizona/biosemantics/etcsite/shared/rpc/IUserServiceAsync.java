package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public interface IUserServiceAsync {

	public void getUsers(AuthenticationToken authenticationToken, boolean includeSelf, AsyncCallback<RPCResult<List<ShortUser>>> callback);
	
}

package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISetupServiceAsync {

	public void getSetup(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Setup>> callback);
	
}

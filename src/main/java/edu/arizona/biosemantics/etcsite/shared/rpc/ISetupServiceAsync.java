package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISetupServiceAsync {

	public void getSetup(AsyncCallback<RPCResult<Setup>> callback);
	
}

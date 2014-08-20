package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Setup;

public interface ISetupServiceAsync {

	public void getSetup(AsyncCallback<RPCResult<Setup>> callback);
	
}

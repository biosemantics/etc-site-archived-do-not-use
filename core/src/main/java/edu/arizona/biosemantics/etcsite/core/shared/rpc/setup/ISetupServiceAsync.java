package edu.arizona.biosemantics.etcsite.core.shared.rpc.setup;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.core.shared.model.Setup;

public interface ISetupServiceAsync {

	public void getSetup(AsyncCallback<Setup> callback);
	
	public void getNews(AsyncCallback<String> callback);
	
}

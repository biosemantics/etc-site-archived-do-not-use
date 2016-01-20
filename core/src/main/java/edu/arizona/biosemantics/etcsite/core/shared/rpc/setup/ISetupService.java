package edu.arizona.biosemantics.etcsite.core.shared.rpc.setup;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.Setup;

@RemoteServiceRelativePath("setup")
public interface ISetupService extends RemoteService {

	public Setup getSetup();

	String getNews() throws Exception;

}

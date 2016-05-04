package edu.arizona.biosemantics.etcsite.shared.rpc.file.setup;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Setup;

@RemoteServiceRelativePath("setup")
public interface ISetupService extends RemoteService {

	public Setup getSetup();

	String getNews() throws Exception;

}

package edu.arizona.biosemantics.etcsite.server.rpc.setup;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Setup;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.setup.ISetupService;

public class SetupService extends RemoteServiceServlet implements ISetupService {
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Setup getSetup() {		
		String seperator = File.separator;
		Setup setup = new Setup();
		setup.setSeperator(seperator);
		setup.setFileBase(Configuration.fileBase);
		setup.setGoogleClientId(Configuration.googleClientId);
		setup.setGoogleRedirectURI(Configuration.googleRedirectURI);
		return setup;
	}

}

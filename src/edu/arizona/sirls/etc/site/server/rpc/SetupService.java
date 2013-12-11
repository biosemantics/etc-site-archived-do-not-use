package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ISetupService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.Setup;

public class SetupService extends RemoteServiceServlet implements ISetupService {
	
	@Override
	public RPCResult<Setup> getSetup(AuthenticationToken authenticationToken) {		
		String seperator = File.separator;
		Setup setup = new Setup();
		setup.setSeperator(seperator);
		setup.setOtoLiteURL(Configuration.otoLiteURL);
		setup.setFileBase(Configuration.fileBase);
		return new RPCResult<Setup>(true, setup);
	}

}

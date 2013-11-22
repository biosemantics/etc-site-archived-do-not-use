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

	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public RPCResult<Setup> getSetup(AuthenticationToken authenticationToken) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Setup>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Setup>(false, "Authentication failed");
		
		String seperator = File.separator;
		Setup setup = new Setup();
		setup.setSeperator(seperator);
		setup.setOtoLiteURL(Configuration.otoLiteURL);
		setup.setFileBase(Configuration.fileBase);
		return new RPCResult<Setup>(true, setup);
	}

}

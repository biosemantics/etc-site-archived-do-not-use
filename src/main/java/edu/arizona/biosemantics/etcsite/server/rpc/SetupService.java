package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISetupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.Setup;

public class SetupService extends RemoteServiceServlet implements ISetupService {
	
	@Override
	public RPCResult<Setup> getSetup() {		
		String seperator = File.separator;
		Setup setup = new Setup();
		setup.setSeperator(seperator);
		setup.setOtoLiteURL(Configuration.otoLiteURL);
		setup.setFileBase(Configuration.fileBase);
		return new RPCResult<Setup>(true, setup);
	}

}

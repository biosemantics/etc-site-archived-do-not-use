package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("setup")
public interface ISetupService extends RemoteService {

	public RPCResult<Setup> getSetup(AuthenticationToken authenticationToken);

}

package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

@RemoteServiceRelativePath("user")
public interface IUserService extends RemoteService {

	public RPCResult<List<ShortUser>> getUsers(AuthenticationToken authenticationToken);
	
}

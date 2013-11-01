package edu.arizona.sirls.etc.site.server.rpc;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IUserService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;

public class UserService extends RemoteServiceServlet implements IUserService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public RPCResult<List<ShortUser>> getUsers(AuthenticationToken authenticationToken) {
		RPCResult<List<ShortUser>> result = new RPCResult<List<ShortUser>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			try {
				List<ShortUser> usernames = UserDAO.getInstance().getUsers();
				result = new RPCResult<List<ShortUser>>(true, usernames);
			} catch (Exception e) {
				e.printStackTrace();
				result = new RPCResult<List<ShortUser>>(false, "Internal Server Error");
			}
		}
		return result;
	}

}

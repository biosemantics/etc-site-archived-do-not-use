package edu.arizona.biosemantics.etcsite.server.rpc;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class UserService extends RemoteServiceServlet implements IUserService {
	
	@Override
	public RPCResult<List<ShortUser>> getUsers(AuthenticationToken authenticationToken, boolean includeSelf) {
		try {
			List<ShortUser> usernames;
			if(includeSelf)
				usernames = UserDAO.getInstance().getUsers();
			else
				usernames = UserDAO.getInstance().getUsersWithout(authenticationToken.getUsername());
			return new RPCResult<List<ShortUser>>(true, usernames);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<List<ShortUser>>(false, "Internal Server Error");
		}
	}

}

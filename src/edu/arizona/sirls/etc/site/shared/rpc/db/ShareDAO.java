package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ShareDAO {

	private static ShareDAO instance;
	
	public Share getShare(int id) throws SQLException, ClassNotFoundException, IOException {
		Share share = null;
		Query query = new Query("SELECT * FROM shares WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int taskId = result.getInt(2);
			Date created = result.getTimestamp(3);
			Task task = TaskDAO.getInstance().getTask(taskId);
			
			List<User> invitees = new LinkedList<User>();
			Query inviteesQuery = new Query("SELECT inviteeuser FROM shareinvitees WHERE share = " + id);
			ResultSet resultInvitees = inviteesQuery.execute();
			while(resultInvitees.next()) {
				int userId = resultInvitees.getInt(1);
				invitees.add(UserDAO.getInstance().getUser(userId));
			}
			share = new Share(id, task, invitees, created);
		}
		query.close();
		return share;
	}

	public static ShareDAO getInstance() {
		if(instance == null)
			instance = new ShareDAO();
		return instance;
	}
	
}

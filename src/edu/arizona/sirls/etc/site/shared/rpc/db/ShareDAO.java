package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ShareDAO {

	private static ShareDAO instance;
	
	public static ShareDAO getInstance() {
		if(instance == null)
			instance = new ShareDAO();
		return instance;
	}
	
	public Share getShare(int id) throws SQLException, ClassNotFoundException, IOException {
		Share share = null;
		Query query = new Query("SELECT * FROM shares WHERE id = ?");
		query.setParameter(1, id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			int taskId = result.getInt(2);
			Date created = result.getTimestamp(3);
			Task task = TaskDAO.getInstance().getTask(taskId);
			
			Set<ShortUser> invitees = new HashSet<ShortUser>();
			Query inviteesQuery = new Query("SELECT inviteeuser FROM shareinvitees WHERE share = ?");
			inviteesQuery.setParameter(1, id);
			ResultSet resultInvitees = inviteesQuery.execute();
			while(resultInvitees.next()) {
				int userId = resultInvitees.getInt(1);
				invitees.add(UserDAO.getInstance().getShortUser(userId));
			}
			share = new Share(id, task, invitees, created);
		}
		query.close();
		return share;
	}

	public Share addShare(Share share) throws SQLException, ClassNotFoundException, IOException {
		Share result = null;
		Query query = new Query("INSERT INTO `shares` (`task`) VALUES(?)");
		query.setParameter(1, share.getTask().getId());
		query.execute();
		ResultSet generatedKeys = query.getGeneratedKeys();
        if (generatedKeys.next()) {
            result = this.getShare(generatedKeys.getInt(1));
            
            for(ShortUser invitee : share.getInvitees()) {
	            Query inviteeQuery = new Query("INSERT INTO `shareinvitees` (`share`, `inviteeuser`) VALUES (?, ?)");
	            inviteeQuery.setParameter(1, result.getId());
	            inviteeQuery.setParameter(2, invitee.getId());
	            inviteeQuery.executeAndClose();
            }
        }
		query.close();
		return result;
	}

	public List<Share> getSharesOfOwner(ShortUser owner) throws ClassNotFoundException, SQLException, IOException {
		List<Share> result = new LinkedList<Share>();
		Query query = new Query("SELECT id FROM `shares` WHERE shares.id=tasks.id AND tasks.user=?");
		query.setParameter(1,  owner.getId());
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			Share share = this.getShare(resultSet.getInt(1));
			result.add(share);
		}
		return result;
	}
	
	public List<Share> getSharesOfInvitee(ShortUser invitee) throws ClassNotFoundException, SQLException, IOException {
		List<Share> result = new LinkedList<Share>();
		Query query = new Query("SELECT share FROM `shareinvitees` WHERE inviteeuser=?");
		query.setParameter(1, invitee.getId());
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			Share share = this.getShare(resultSet.getInt(1));
			result.add(share);
		}
		return result;
	}
	
}

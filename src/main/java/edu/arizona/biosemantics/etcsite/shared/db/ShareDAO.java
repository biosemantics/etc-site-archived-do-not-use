package edu.arizona.biosemantics.etcsite.shared.db;

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
			inviteesQuery.close();
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
		query.close();
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
		query.close();
		return result;
	}

	public List<Share> getShares(Task task) throws ClassNotFoundException, SQLException, IOException {
		List<Share> shares = new LinkedList<Share>();
		Query query = new Query("SELECT id FROM shares WHERE task = ?");
		query.setParameter(1, task.getId());
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			Share share = this.getShare(resultSet.getInt(1));
			shares.add(share);
		}
		query.close();
		return shares;
	}

	public void removeShare(Share share) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM shareinvitees WHERE share = ?");
		query.setParameter(1, share.getId());
		query.executeAndClose();
		query = new Query("DELETE FROM shares WHERE id = ?");
		query.setParameter(1, share.getId());
		query.executeAndClose();
	}

	public Set<ShortUser> getInvitees(Task task) throws ClassNotFoundException, SQLException, IOException {
		Set<ShortUser> invitees = new HashSet<ShortUser>();
		List<Share> shares = this.getShares(task);
		for(Share share : shares) {
			invitees.addAll(share.getInvitees());
		}
		return invitees;
	}

	public Share addOrUpdateShare(Share share) throws ClassNotFoundException, SQLException, IOException {
		List<Share> tasksShares = this.getShares(share.getTask());
		if(!tasksShares.isEmpty()) {
			Share shareToUpdate = tasksShares.get(0);
			shareToUpdate.setInvitees(share.getInvitees());
			this.updateShare(shareToUpdate);
			return shareToUpdate;
		} else 
			return this.addShare(share);
	}

	public Share updateShare(Share share) throws SQLException, ClassNotFoundException, IOException {
		Query removeInvitees = new Query("DELETE FROM shareinvitees WHERE share = ?");
		removeInvitees.setParameter(1, share.getId());
		removeInvitees.executeAndClose();
		
        for(ShortUser invitee : share.getInvitees()) {
            Query inviteeQuery = new Query("INSERT INTO `shareinvitees` (`share`, `inviteeuser`) VALUES (?, ?)");
            inviteeQuery.setParameter(1, share.getId());
            inviteeQuery.setParameter(2, invitee.getId());
            inviteeQuery.executeAndClose();
        }
        return share;
	}

	public List<Share> getSharesOfInviteeForTask(ShortUser invitee, Task task) throws SQLException, ClassNotFoundException, IOException {
		List<Share> result = new LinkedList<Share>();
		Query query = new Query("SELECT share FROM shareinvitees, shares, tasks WHERE tasks.id = shares.task AND shares.id = shareinvitees.share AND inviteeuser=? AND tasks.id = ?");
		query.setParameter(1, invitee.getId());
		query.setParameter(2, task.getId());
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			Share share = this.getShare(resultSet.getInt(1));
			result.add(share);
		}
		query.close();
		return result;
	}
	
}

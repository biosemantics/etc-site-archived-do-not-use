package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class ShareDAO {
	
	private UserDAO userDAO;
	private TaskDAO taskDAO;
		
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	public Share getShare(int id) {
		Share share = null;
		try (Query query = new Query("SELECT * FROM etcsite_shares WHERE id = ?")) {
			query.setParameter(1, id);
			query.execute();
			ResultSet result = query.getResultSet();
			while(result.next()) {
				id = result.getInt(1);
				int taskId = result.getInt(2);
				Date created = result.getTimestamp(3);
				Task task = taskDAO.getTask(taskId);
				
				Set<ShortUser> invitees = new HashSet<ShortUser>();
				try(Query inviteesQuery = new Query("SELECT inviteeuser FROM etcsite_shareinvitees WHERE share = ?")) {
					inviteesQuery.setParameter(1, id);
					ResultSet resultInvitees = inviteesQuery.execute();
					while(resultInvitees.next()) {
						int userId = resultInvitees.getInt(1);
						invitees.add(userDAO.getShortUser(userId));
					}
				}
				share = new Share(id, task, invitees, created);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get share", e);
		}
		return share;
	}

	public Share addShare(Share share) {
		Share result = null;
		try(Query query = new Query("INSERT INTO `etcsite_shares` (`task`) VALUES(?)")) {
			query.setParameter(1, share.getTask().getId());
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            result = this.getShare(generatedKeys.getInt(1));
	            
	            for(ShortUser invitee : share.getInvitees()) {
		            try(Query inviteeQuery = new Query("INSERT INTO `etcsite_shareinvitees` (`share`, `inviteeuser`) VALUES (?, ?)")) {
			            inviteeQuery.setParameter(1, result.getId());
			            inviteeQuery.setParameter(2, invitee.getId());
			            inviteeQuery.execute();
		            }
	            }
	        }
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert share", e);
		}
		return result;
	}

	public List<Share> getSharesOfOwner(ShortUser owner) {
		List<Share> result = new LinkedList<Share>();
		try(Query query = new Query("SELECT id FROM `etcsite_shares` WHERE shares.id=tasks.id AND tasks.user=?")) {
			query.setParameter(1,  owner.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				Share share = this.getShare(resultSet.getInt(1));
				result.add(share);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get shares of owner", e);
		}
		return result;
	}
	
	public List<Share> getSharesOfInvitee(ShortUser invitee) {
		List<Share> result = new LinkedList<Share>();
		try(Query query = new Query("SELECT share FROM `etcsite_shareinvitees` WHERE inviteeuser=?")) {
			query.setParameter(1, invitee.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				Share share = this.getShare(resultSet.getInt(1));
				result.add(share);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get shares of invitee", e);
		}
		return result;
	}

	public List<Share> getShares(Task task) {
		List<Share> shares = new LinkedList<Share>();
		try(Query query = new Query("SELECT id FROM etcsite_shares WHERE task = ?")) {
			query.setParameter(1, task.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				Share share = this.getShare(resultSet.getInt(1));
				shares.add(share);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get shares of task", e);
		}
		return shares;
	}

	public void removeShare(Share share) {
		try (Query query = new Query("DELETE FROM etcsite_shareinvitees WHERE share = ?")) {
			query.setParameter(1, share.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove share invitees", e);
		}
		try(Query query = new Query("DELETE FROM etcsite_shares WHERE id = ?")) {
			query.setParameter(1, share.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove share", e);
		}
	}

	public Set<ShortUser> getInvitees(Task task) {
		Set<ShortUser> invitees = new HashSet<ShortUser>();
		List<Share> shares = this.getShares(task);
		for(Share share : shares) {
			invitees.addAll(share.getInvitees());
		}
		return invitees;
	}

	public Share addOrUpdateShare(Share share) {
		List<Share> tasksShares = this.getShares(share.getTask());
		if(!tasksShares.isEmpty()) {
			Share shareToUpdate = tasksShares.get(0);
			shareToUpdate.setInvitees(share.getInvitees());
			this.updateShare(shareToUpdate);
			return shareToUpdate;
		} else 
			return this.addShare(share);
	}

	public Share updateShare(Share share) {
		try(Query removeInvitees = new Query("DELETE FROM etcsite_shareinvitees WHERE share = ?")) {
			removeInvitees.setParameter(1, share.getId());
			removeInvitees.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update share", e);
		}
		
        for(ShortUser invitee : share.getInvitees()) {
            try(Query inviteeQuery = new Query("INSERT INTO `etcsite_shareinvitees` (`share`, `inviteeuser`) VALUES (?, ?)")) {
	            inviteeQuery.setParameter(1, share.getId());
	            inviteeQuery.setParameter(2, invitee.getId());
	            inviteeQuery.execute();
            } catch(Exception e) {
            	log(LogLevel.ERROR, "Couldn't insert share invitees", e);
            }
        }
        return share;
	}

	public List<Share> getSharesOfInviteeForTask(ShortUser invitee, Task task)  {
		List<Share> result = new LinkedList<Share>();
		try(Query query = new Query("SELECT share FROM etcsite_shareinvitees, " +
				"etcsite_shares, etcsite_tasks WHERE etcsite_tasks.id = etcsite_shares.task AND etcsite_shares.id = etcsite_shareinvitees.share " +
				"AND inviteeuser=? AND etcsite_tasks.id = ?")) {
			query.setParameter(1, invitee.getId());
			query.setParameter(2, task.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				Share share = this.getShare(resultSet.getInt(1));
				result.add(share);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get shares of invitee for task", e);
		}
		return result;
	}
	
}

package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserDAO {

	private static UserDAO instance;

	public ShortUser getShortUser(int userId) throws ClassNotFoundException, SQLException, IOException {
		User user = this.getUser(userId);
		ShortUser result = new ShortUser(user.getId(), user.getEmail());
		return result;
	}

	public User getUser(int id) throws SQLException, ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query("SELECT * FROM useraccounts WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while (result.next()) {
			id = result.getInt(1);
			String openIdProviderId = result.getString(2);
			String openIdProvider = result.getString(3);
			String password = result.getString(4);
			String firstName = result.getString(5);
			String lastName = result.getString(6);
			String email = result.getString(7);
			String affiliation = result.getString(8);
			String bioportalUserId = result.getString(9);
			String bioportalAPIKey = result.getString(10);
			Date created = result.getTimestamp(11);

			user = new User(id, openIdProviderId, openIdProvider, password,
					firstName, lastName, email, affiliation, bioportalUserId,
					bioportalAPIKey, created);
		}
		query.close();
		return user;
	}

	public User getLocalUserWithEmail(String email) throws SQLException,
			ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query(
				"SELECT * FROM useraccounts WHERE (openidproviderid) = ?");
		query.setParameter(1, email);
		ResultSet result = query.execute();
		while (result.next()) {
			int id = result.getInt(1);
			String openIdProviderId = result.getString(2);
			String openIdProvider = result.getString(3);
			String password = result.getString(4);
			String firstName = result.getString(5);
			String lastName = result.getString(6);
			String emailAddress = result.getString(7);
			String affiliation = result.getString(8);
			String bioportalUserId = result.getString(9);
			String bioportalAPIKey = result.getString(10);
			Date created = result.getTimestamp(11);

			user = new User(id, openIdProviderId, openIdProvider, password,
					firstName, lastName, emailAddress, affiliation,
					bioportalUserId, bioportalAPIKey, created);
		}
		query.close();
		return user;
	}
	
	public boolean addUser(String email, String encryptedPassword, String firstName, String lastName) throws ClassNotFoundException, SQLException, IOException{
		if (getLocalUserWithEmail(email) != null){ //if this user already exists, return false. 
			return false;
		} else {
			Query addUser = new Query(
					"INSERT INTO `useraccounts`(`id`, `openidproviderid`, `openidprovider`, `password`, `firstname`, `lastname`, `email`, `affiliation`, `bioportaluserid`, `bioportalapikey`, `created`) VALUES (LAST_INSERT_ID(), ?, \"none\", ?, ?, ?, ?, \"\", \"\", \"\", CURRENT_TIMESTAMP)");
			addUser.setParameter(1, email);
			addUser.setParameter(2, encryptedPassword);
			addUser.setParameter(3, firstName);
			addUser.setParameter(4, lastName);
			addUser.setParameter(5, email);
			addUser.execute();
			
			return true;
		}
	}
	
	public boolean updateUser(int id, String firstName, String lastName, String email,
			String password, String affiliation, String bioportalUserId,
			String bioportalAPIKey) throws ClassNotFoundException, SQLException, IOException{
		
		if (this.getUser(id) == null){ //if this user does not exist, return false.
			return false;
		} else {
			Query updateUser = new Query(
					"UPDATE `useraccounts` SET `firstname`=?, `lastname`=?, `email`=?, `password`=?, `affiliation`=?, `bioportaluserid`=?, `bioportalapikey`=? WHERE (id) = ?");
				
			updateUser.setParameter(1, firstName);
			updateUser.setParameter(2, lastName);
			updateUser.setParameter(3, email);
			updateUser.setParameter(4, password);
			updateUser.setParameter(5, affiliation);
			updateUser.setParameter(6, bioportalUserId);
			updateUser.setParameter(7, bioportalAPIKey);
			updateUser.setParameter(8, id);
			updateUser.execute();
			
			return true;
		}
	}

	public static UserDAO getInstance() {
		if (instance == null)
			instance = new UserDAO();
		return instance;
	}

	public List<ShortUser> getUsers() throws SQLException,
			ClassNotFoundException, IOException {
		List<ShortUser> result = new LinkedList<ShortUser>();
		Query query = new Query("SELECT id FROM useraccounts");
		ResultSet resultSet = query.execute();
		while (resultSet.next()) {
			result.add(new ShortUser(resultSet.getInt(1), resultSet
					.getString(1))); // see note in ShortUser. _ags
		}
		query.close();
		return result;
	}

	public List<ShortUser> getUsersWithout(int userId)
			throws SQLException, ClassNotFoundException, IOException {
		List<ShortUser> result = new LinkedList<ShortUser>();
		Query query = new Query("SELECT id FROM useraccounts WHERE id != ?");
		query.setParameter(1, userId);
		ResultSet resultSet = query.execute();
		while (resultSet.next()) {
			result.add(new ShortUser(resultSet.getInt(1), resultSet
					.getString(1))); // see note in ShortUser. _ags
		}
		query.close();
		return result;
	}
}

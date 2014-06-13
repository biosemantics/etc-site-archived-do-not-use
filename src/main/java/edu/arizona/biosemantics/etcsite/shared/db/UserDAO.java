package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.etcsite.server.Configuration;

public class UserDAO {

	private static UserDAO instance;

	public ShortUser getShortUser(int userId) throws ClassNotFoundException, SQLException, IOException {
		User user = this.getUser(userId);
		return createShortUser(user);
	}

	private ShortUser createShortUser(User user) {
		return new ShortUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getAffiliation());
	}

	public User getUser(int id) throws SQLException, ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query("SELECT * FROM useraccounts WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while (result.next()) {
			user = createUser(result);
		}
		query.close();
		return user;
	}

	private User createUser(ResultSet result) throws SQLException {
		int id = result.getInt(1);
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

		return new User(id, openIdProviderId, openIdProvider, password,
				firstName, lastName, email, affiliation, bioportalUserId,
				bioportalAPIKey, created);
	}

	public User getUser(String email, String openIdProvider) throws SQLException,
			ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query(
				"SELECT * FROM useraccounts WHERE (openidproviderid) = ? AND openidprovider = ?");
		query.setParameter(1, email);
		query.setParameter(2, openIdProvider);
		ResultSet result = query.execute();
		while (result.next()) {
			int id = result.getInt(1);
			String openIdProviderId = result.getString(2);
			openIdProvider = result.getString(3);
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
	
	public boolean addUser(String openIdProviderId, String encryptedPassword, String firstName, String lastName, String openIdProvider) throws ClassNotFoundException, SQLException, IOException{
		if (getUser(openIdProviderId, openIdProvider) != null){ //if this user already exists, return false. 
			return false;
		} else {
			Query addUser = new Query(
					"INSERT INTO `useraccounts`(`id`, `openIdProviderId`, `openidprovider`, `password`, `firstname`, `lastname`, `email`, `affiliation`, `bioportaluserid`, `bioportalapikey`, `created`) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?, ?, ?, \"\", \"\", \"\", CURRENT_TIMESTAMP)");
			addUser.setParameter(1, openIdProviderId);
			addUser.setParameter(2, openIdProvider);
			addUser.setParameter(3, encryptedPassword);
			addUser.setParameter(4, firstName);
			addUser.setParameter(5, lastName);
			addUser.setParameter(6, openIdProviderId); //email
			addUser.execute();
			
			//add a file directory for this user - name of directory is id. 
			int id = getUser(openIdProviderId, openIdProvider).getId();	
			String filename = Configuration.fileBase + File.separator + id;
			new File(filename).mkdirs();
			
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
		Query query = new Query("SELECT * FROM useraccounts");
		ResultSet resultSet = query.execute();
		while (resultSet.next()) {
			result.add(createShortUser(createUser(resultSet)));
		}
		query.close();
		return result;
	}

	public List<ShortUser> getUsersWithout(int userId)
			throws SQLException, ClassNotFoundException, IOException {
		List<ShortUser> result = new LinkedList<ShortUser>();
		Query query = new Query("SELECT * FROM useraccounts WHERE id != ?");
		query.setParameter(1, userId);
		ResultSet resultSet = query.execute();
		while (resultSet.next()) {
			result.add(createShortUser(createUser(resultSet)));
		}
		query.close();
		return result;
	}
}

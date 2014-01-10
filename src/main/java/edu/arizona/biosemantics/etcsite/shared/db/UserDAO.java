package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserDAO {

	private static UserDAO instance;
	
	public ShortUser getShortUser(int id) throws ClassNotFoundException, SQLException, IOException {
		User user = this.getUser(id);
		ShortUser result = new ShortUser(user.getId(), user.getName());
		return result;
	}
	
	public ShortUser getShortUser(String name) throws ClassNotFoundException, SQLException, IOException {
		User user = this.getUser(name);
		ShortUser result = new ShortUser(user.getId(), user.getName());
		return result;
	}
	
	public User getUser(int id) throws SQLException, ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query("SELECT * FROM users WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			String password = result.getString(3);
			String bioportalUserId = result.getString(4);
			String bioportalAPIKey = result.getString(5);
			Date created = result.getTimestamp(6);
			user = new User(id, name, password, bioportalUserId, bioportalAPIKey, created);
		}
		query.close();
		return user;
	}
	
	public User getUser(String name) throws SQLException, ClassNotFoundException, IOException {		
		User user = null;
		Query query = new Query("SELECT * FROM users WHERE name = ?");
		query.setParameter(1, name);
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			String password = result.getString(3);
			String bioportalUserId = result.getString(4);
			String bioportalAPIKey = result.getString(5);
			Date created = result.getTimestamp(6);
			user = new User(id, name, password, bioportalUserId, bioportalAPIKey, created);
		}
		query.close();
		return user;
	}
	
	public void setPassword(User user, String newPassword) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE users SET password = ? WHERE id = ?");
		query.setParameter(1, newPassword);
		query.setParameter(2, user.getId());
		query.executeAndClose();
	}
		
	public static UserDAO getInstance() {
		if(instance == null)
			instance = new UserDAO();
		return instance;
	}

	public List<ShortUser> getUsers() throws SQLException, ClassNotFoundException, IOException {
		List<ShortUser> result = new LinkedList<ShortUser>();
		Query query = new Query("SELECT id, name FROM users");
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			result.add(new ShortUser(resultSet.getInt(1), resultSet.getString(2)));
		}
		return result;
	}
	
	public List<ShortUser> getUsersWithout(String username) throws SQLException, ClassNotFoundException, IOException {
		List<ShortUser> result = new LinkedList<ShortUser>();
		Query query = new Query("SELECT id, name FROM users WHERE name != ?");
		query.setParameter(1, username);
		ResultSet resultSet = query.execute();
		while(resultSet.next()) {
			result.add(new ShortUser(resultSet.getInt(1), resultSet.getString(2)));
		}
		return result;
	}

}

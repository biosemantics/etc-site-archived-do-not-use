package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserDAO {

	private static UserDAO instance;
	
	public User getUser(int id) throws SQLException, ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query("SELECT * FROM users WHERE id = " + id);
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
		Query query = new Query("SELECT * FROM users WHERE name = '" + name + "'");
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
		Query query = new Query("UPDATE users SET password = '" + newPassword + "' WHERE id = " + user.getId());
		query.executeAndClose();
	}
		
	public static UserDAO getInstance() {
		if(instance == null)
			instance = new UserDAO();
		return instance;
	}

}

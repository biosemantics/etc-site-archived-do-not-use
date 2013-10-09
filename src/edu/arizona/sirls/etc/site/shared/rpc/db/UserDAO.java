package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends AbstractDAO {

	private static UserDAO instance;

	public UserDAO() throws IOException, ClassNotFoundException, SQLException {
		super();
	}
	
	public User getUser(int id) throws SQLException, ClassNotFoundException, IOException {
		User user = null;
		Query query = new Query("SELECT * FROM users WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			String password = result.getString(3);
			String bioportalUserId = result.getString(4);
			String bioportalAPIKey = result.getString(5);
			long created = result.getLong(6);
			user = new User(id, name, password, bioportalUserId, bioportalAPIKey, created);
		}
		query.close();
		return user;
	}
	
	public User getUser(String name) throws SQLException, ClassNotFoundException, IOException {		
		User user = null;
		Query query = new Query("SELECT * FROM users WHERE name = '" + name + "'");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			String password = result.getString(3);
			String bioportalUserId = result.getString(4);
			String bioportalAPIKey = result.getString(5);
			long created = result.getLong(6);
			user = new User(id, name, password, bioportalUserId, bioportalAPIKey, created);
		}
		query.close();
		return user;
	}
	
	public void setPassword(User user, String newPassword) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE users SET password = '" + newPassword + "' WHERE id = " + user.getId());
		query.executeAndClose();
	}
		
	public static UserDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new UserDAO();
		return instance;
	}

}

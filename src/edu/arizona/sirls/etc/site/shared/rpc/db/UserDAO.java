package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends AbstractDAO {

	private static UserDAO instance;

	public UserDAO() throws IOException, ClassNotFoundException {
		super();
	}
	
	public User getUser(int id) throws SQLException {
		User user = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM users WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			String password = result.getString(3);
			long created = result.getLong(4);
			user = new User(id, name, password, created);
		}
		this.closeConnection();
		return user;
	}
	
	public User getUser(String name) throws SQLException {		
		User user = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM users WHERE name = '" + name + "'");
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			String password = result.getString(3);
			long created = result.getLong(4);
			user = new User(id, name, password, created);
		}
		this.closeConnection();
		return user;
	}
	
	public void setPassword(User user, String newPassword) throws SQLException {
		this.openConnection();
		this.executeSQL("UPDATE users SET password = '" + newPassword + "' WHERE id = " + user.getId());		
		this.closeConnection();
	}
		
	public static UserDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new UserDAO();
		return instance;
	}

}

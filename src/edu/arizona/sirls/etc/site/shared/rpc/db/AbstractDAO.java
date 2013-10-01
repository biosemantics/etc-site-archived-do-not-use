package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public abstract class AbstractDAO {
	
	protected ConnectionPool connectionPool;

	public AbstractDAO() throws ClassNotFoundException, SQLException, IOException {
		this.connectionPool = ConnectionPool.getInstance();
	}
	
	public PreparedStatement executeSQL(String sql, Connection connection) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		preparedStatement.execute();
		return preparedStatement;
	}
	
	public PreparedStatement prepareStatement(String sql, Connection connection) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
}
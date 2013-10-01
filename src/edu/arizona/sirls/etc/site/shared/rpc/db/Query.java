package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {

	private String sql;
	private ResultSet resultSet;
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	public Query(String sql) {
		this.sql = sql;
	}
	
	public void execute() throws SQLException, ClassNotFoundException, IOException {
		connection = ConnectionPool.getInstance().getConnection();
		preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
	}
	
	public ResultSet getResultSet() { 
		return resultSet;
	}
	
	public void close() throws SQLException {
		if(resultSet != null)
			resultSet.close();
		if(preparedStatement != null)
			preparedStatement.close();
		if(connection != null)
			connection.close();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return preparedStatement.getGeneratedKeys();
	}

	public void executeAndClose() throws SQLException, ClassNotFoundException, IOException {
		this.execute();
		this.close();
	}
	
}

package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Query {

	private String sql;
	private ResultSet resultSet;
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	public Query(String sql) throws ClassNotFoundException, SQLException, IOException {
		this.sql = sql;
		connection = ConnectionPool.getInstance().getConnection();
		preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	}
	
	public void setParameter(int index, String parameter) throws SQLException {
		preparedStatement.setString(index, parameter);
	}
	
	public void setParameter(int index, boolean parameter) throws SQLException {
		preparedStatement.setBoolean(index, parameter);
	}
	
	public void setParameter(int index, int parameter) throws SQLException {
		preparedStatement.setInt(index, parameter);
	}
	
	public void setParameter(int index, Timestamp parameter) throws SQLException {
		preparedStatement.setTimestamp(index, parameter);
	}
	
	public ResultSet execute() throws SQLException {
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		return resultSet;
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
	
	public String toString() {
		return sql;
	}
}

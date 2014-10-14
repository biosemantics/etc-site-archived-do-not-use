package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;

public class Query implements AutoCloseable {

	public static class QueryException extends Exception {
		
		public QueryException(String message) {
			super(message);
		}
		
		public QueryException(String message, Throwable cause) {
			super(message, cause);
			if(cause != null)
				log(LogLevel.ERROR, message, cause);
		}
		
		public QueryException(Throwable cause) {
			super(cause);
			if(cause != null)
				log(LogLevel.ERROR, "Query exception occured", cause);
		}
		
	}

	public static ConnectionPool connectionPool;
	
	private String sql;
	private ResultSet resultSet;
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	public Query(String sql) throws QueryException {
		this.sql = sql;
		try {
			connection = connectionPool.getConnection();
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public void setParameter(int index, String parameter) throws QueryException {
		try {
			preparedStatement.setString(index, parameter);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public void setParameter(int index, boolean parameter) throws QueryException {
		try {
			preparedStatement.setBoolean(index, parameter);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public void setParameter(int index, int parameter) throws QueryException {
		try {
			preparedStatement.setInt(index, parameter);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public void setParameter(int index, Timestamp parameter) throws QueryException {
		try {
			preparedStatement.setTimestamp(index, parameter);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public ResultSet execute() throws QueryException {
		try {
			preparedStatement.execute();
			resultSet = preparedStatement.getResultSet();
			return resultSet;
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public ResultSet getResultSet() { 
		return resultSet;
	}
	
	@Override
	public void close() throws QueryException {
		try {
			if(resultSet != null)
				resultSet.close();
			if(preparedStatement != null)
				preparedStatement.close();
			if(connection != null)
				connection.close();
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}

	public ResultSet getGeneratedKeys() throws QueryException { 
		try {
			return preparedStatement.getGeneratedKeys();
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}

	private void executeAndClose() throws QueryException {
		try {
			this.execute();
			this.close();
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public String toString() {
		return sql;
	}
}

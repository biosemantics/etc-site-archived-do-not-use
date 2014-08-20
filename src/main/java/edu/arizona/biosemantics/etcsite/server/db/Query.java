package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Query implements AutoCloseable {

	public static class QueryException extends Exception {
		
		public QueryException(String message) {
			super(message);
		}
		
		public QueryException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public QueryException(Throwable cause) {
			super(cause);
		}
		
	}
	
	private String sql;
	private ResultSet resultSet;
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	public Query(String sql) throws QueryException {
		this.sql = sql;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} catch(Exception e) {
			throw new QueryException(e.getMessage(), e.getCause());
		}
	}
	
	public Query(String sql, String pool) throws QueryException {
		this.sql = sql;
		try {
			connection = ConnectionPool.getInstance().getConnection(pool);
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

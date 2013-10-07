package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class ConnectionPool {

	private static ConnectionPool instance;
	private BoneCP connectionPool;
	
	public static ConnectionPool getInstance() throws ClassNotFoundException, SQLException, IOException { 
		if(instance == null)
			instance = new ConnectionPool();
		return instance;
	}
	
	public ConnectionPool() throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties(); 
		properties.load(loader.getResourceAsStream("config.properties"));

		String databaseName = properties.getProperty("databaseName");
		String databaseUser = properties.getProperty("databaseUser");
		String databasePassword = properties.getProperty("databasePassword");
		String jdbcUrl = "jdbc:mysql://localhost:3306/" + databaseName + "?connecttimeout=0&sockettimeout=0&autoreconnect=true";
		
		// setup the connection pool
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(jdbcUrl); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		config.setUsername(databaseUser); 
		config.setPassword(databasePassword);
		config.setMinConnectionsPerPartition(10);
		config.setMaxConnectionsPerPartition(20);
		config.setPartitionCount(2);
		connectionPool = new BoneCP(config);
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection(); // fetch a connection
	}
	
	public void shutdown() {
		this.connectionPool.shutdown();
	}
	
}

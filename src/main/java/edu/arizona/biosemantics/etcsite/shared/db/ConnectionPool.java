package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class ConnectionPool {

	private static ConnectionPool instance;
	private BoneCP connectionPool;
	private BoneCP connectionPoolOtoLite;
	private Driver mySqlDriver;
	
	public static ConnectionPool getInstance() throws ClassNotFoundException, SQLException, IOException { 
		if(instance == null)
			instance = new ConnectionPool();
		return instance;
	}
	
	private ConnectionPool() throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		mySqlDriver = DriverManager.getDriver("jdbc:mysql://localhost:3306/");
		
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
		config.setPoolName("etcSitePool");
		config.setDisableJMX(true);
		
		connectionPool = new BoneCP(config);
		
		String otoLiteDatabaseName = properties.getProperty("otolite_databaseName");
		String otoLiteDatabaseUser = properties.getProperty("otolite_databaseUser");
		String otoLiteDatabasePassword = properties.getProperty("otolite_databasePassword");
		String otoLiteJdbcUrl = "jdbc:mysql://localhost:3306/" + otoLiteDatabaseName + "?connecttimeout=0&sockettimeout=0&autoreconnect=true";
		
		config = new BoneCPConfig();
		config.setJdbcUrl(otoLiteJdbcUrl); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		config.setUsername(otoLiteDatabaseUser); 
		config.setPassword(otoLiteDatabasePassword);
		config.setMinConnectionsPerPartition(10);
		config.setMaxConnectionsPerPartition(20);
		config.setPartitionCount(2);
		config.setPoolName("otoLitePool");
		config.setDisableJMX(true);
		
		connectionPoolOtoLite = new BoneCP(config);
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	public Connection getConnection(String connection) throws SQLException {
		if(connection.equals("otolite"))
			return connectionPoolOtoLite.getConnection();
		return connectionPool.getConnection();
	}
	
	public void shutdown() {
		this.connectionPool.shutdown();
		try {
			DriverManager.deregisterDriver(mySqlDriver);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
		    AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread t : threadArray) {
			if(t.getName().contains("Abandoned connection cleanup thread") 
		            ||  t.getName().matches("com\\.google.*Finalizer")
		            ) {
		        synchronized(t) {
		            t.stop(); //don't complain, it works
		        }
			}
		}
	}
	
}

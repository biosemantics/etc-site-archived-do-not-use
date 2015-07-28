package edu.arizona.biosemantics.etcsite.server.db;

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

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;

public class ConnectionPool {
	
	private BoneCP connectionPool;
	private Driver mySqlDriver;
	
	public ConnectionPool() throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		mySqlDriver = DriverManager.getDriver("jdbc:mysql://localhost:3306/");
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties(); 
		properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/config.properties"));

		String databaseName = properties.getProperty("databaseName");
		String databaseUser = properties.getProperty("databaseUser");
		String databasePassword = properties.getProperty("databasePassword");
		String jdbcUrl = "jdbc:mysql://localhost:3306/" + databaseName + "?connecttimeout=0&sockettimeout=0&autoreconnect=true";
		
		// setup the connection pool
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(jdbcUrl); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		config.setUsername(databaseUser); 
		config.setPassword(databasePassword);
		config.setMinConnectionsPerPartition(Configuration.database_minConnectionsPerPartition);
		config.setMaxConnectionsPerPartition(Configuration.database_maxConnectionsPerPartition);
		config.setPartitionCount(Configuration.database_partitionCount);
		config.setPoolName("etcSitePool");
		config.setDisableJMX(true);
		
		connectionPool = new BoneCP(config);
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	public Connection getConnection(String connection) throws SQLException {
		return connectionPool.getConnection();
	}
	
	public void shutdown() {
		this.connectionPool.shutdown();
		try {
			DriverManager.deregisterDriver(mySqlDriver);
		} catch (SQLException e) {
			log(LogLevel.ERROR, "Couldn't deregister mysql driver", e);
		}
		try {
		    AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			log(LogLevel.ERROR, "Couldn't shutdown abandoned connection cleanup thread", e);
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

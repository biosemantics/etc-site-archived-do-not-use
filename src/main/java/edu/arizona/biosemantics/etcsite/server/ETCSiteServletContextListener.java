package edu.arizona.biosemantics.etcsite.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.ConnectionPool;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.Query;

public class ETCSiteServletContextListener implements ServletContextListener {

	private ConnectionPool connectionPool;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log(LogLevel.INFO, "Destroy etcsite context " + event.getServletContext().getContextPath());
		try {
			log(LogLevel.INFO, "Shutting down conntection pool");
			connectionPool.shutdown();
			///ConfigurationDAO.getInstance().shutdown();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception shutting down connection pool in etc context", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logSystemProperties();
		logEnvironmentVariables();
		
		log(LogLevel.INFO, "Initializing etcsite context at context path: " + event.getServletContext().getContextPath());
		log(LogLevel.INFO, "Configuration used " + Configuration.asString());
		
		log(LogLevel.INFO, "Install Java logging to SLF4J");
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		
		log(LogLevel.INFO, "Initializing connection pool");
		try {
			connectionPool = new ConnectionPool();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			log(LogLevel.ERROR, "Exception initializing connection pool", e);
		}
		
		if(connectionPool != null) {
			Query.connectionPool = connectionPool;
			DAOManager daoManager = new DAOManager();
			
			log(LogLevel.INFO, "Cleaning up password reset requests");
			//delete all 'old' password reset requests. Should only happen once per server restart. 
			daoManager.getPasswordResetRequestDAO().cleanup();
			
			log(LogLevel.INFO, "Cleaning up captchas");
			// schedule a task to automatically scan and delete old captchas. 
			daoManager.getCaptchaDAO().cleanup();
		}
		
		//set XPath objectmodel for XPath 2 supoprt (saxon)
		System.setProperty("javax.xml.xpath.XPathFactory:" + Configuration.xPathObjectModel, "net.sf.saxon.xpath.XPathFactoryImpl");
	}

	private void logEnvironmentVariables() {
		Map<String, String> variables = System.getenv();
		log(LogLevel.INFO, "Environment variables:");
        for (String key : variables.keySet()) {
            log(LogLevel.INFO, key + " " + variables.get(key));
        }
	}

	private void logSystemProperties() {
		String[] variables = {
				"user.name", 
				"user.home",
				"user.dir",
				"java.home",
				"java.vendor",
				"java.vendor.url",
				"java.version",
				"java.class.path",
				"os.arch",
				"os.name",
				"os.version",
				"file.separator", 
				"path.separator",
				"line.separator",
		};
		
		log(LogLevel.INFO, "Java System Properties");
		for(String variable : variables)
			log(LogLevel.INFO, variable + ": " + System.getProperty("variable"));
	}

}

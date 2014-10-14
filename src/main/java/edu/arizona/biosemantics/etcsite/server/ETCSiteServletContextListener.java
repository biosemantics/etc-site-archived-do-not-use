package edu.arizona.biosemantics.etcsite.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.biosemantics.etcsite.server.db.ConnectionPool;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.Query;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;

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
		log(LogLevel.INFO, "Initializing etcsite context at context path: " + event.getServletContext().getContextPath());
		log(LogLevel.INFO, "Configuration used " + Configuration.asString());
			
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

}

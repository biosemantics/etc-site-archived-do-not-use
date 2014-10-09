package edu.arizona.biosemantics.etcsite.server;

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
		log(LogLevel.INFO, "Destroy etcsite context");
		if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				log(LogLevel.INFO, "Shutting down conntection pool");
				connectionPool.shutdown();
				///ConfigurationDAO.getInstance().shutdown();
			} catch (Exception e) {
				log(LogLevel.ERROR, "Exception shutting down etcsite context", e);
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log(LogLevel.INFO, "Initializing etcsite context");
		log(LogLevel.INFO, "Configuration used " + Configuration.asString());
		
		//if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				log(LogLevel.INFO, "Initializing connection pool");
				connectionPool = new ConnectionPool();
				Query.connectionPool = connectionPool;
				DAOManager daoManager = new DAOManager();
				
				log(LogLevel.INFO, "Cleaning up password reset requests");
				//delete all 'old' password reset requests. Should only happen once per server restart. 
				daoManager.getPasswordResetRequestDAO().cleanup();
				
				log(LogLevel.INFO, "Cleaning up captchas");
				// schedule a task to automatically scan and delete old captchas. 
				daoManager.getCaptchaDAO().cleanup();
				
				//set XPath objectmodel for XPath 2 supoprt (saxon)
				System.setProperty("javax.xml.xpath.XPathFactory:" + Configuration.xPathObjectModel, "net.sf.saxon.xpath.XPathFactoryImpl");
			} catch (Exception e) {
				log(LogLevel.ERROR, "Exception initializing etcsite context", e);
			}
		//}
	}

}

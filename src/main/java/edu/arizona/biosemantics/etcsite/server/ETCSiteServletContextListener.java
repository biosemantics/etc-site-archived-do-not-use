package edu.arizona.biosemantics.etcsite.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.biosemantics.etcsite.server.db.ConnectionPool;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.Query;

public class ETCSiteServletContextListener implements ServletContextListener {

	private ConnectionPool connectionPool;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				connectionPool.shutdown();
				///ConfigurationDAO.getInstance().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				//init connection pool
				connectionPool = new ConnectionPool();
				Query.connectionPool = connectionPool;
				DAOManager daoManager = new DAOManager();
				
				//delete all 'old' password reset requests. Should only happen once per server restart. 
				daoManager.getPasswordResetRequestDAO().cleanup();
				
				// schedule a task to automatically scan and delete old captchas. 
				daoManager.getCaptchaDAO().cleanup();
				
				//set XPath objectmodel for XPath 2 supoprt (saxon)
				System.setProperty("javax.xml.xpath.XPathFactory:" + Configuration.xPathObjectModel, "net.sf.saxon.xpath.XPathFactoryImpl");
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}

}

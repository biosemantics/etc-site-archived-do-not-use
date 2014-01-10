package edu.arizona.biosemantics.etcsite.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.biosemantics.etcsite.shared.db.ConnectionPool;

public class ETCSiteServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				ConnectionPool.getInstance().shutdown();
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
				ConnectionPool connectionPool = ConnectionPool.getInstance();
				
				//set XPath objectmodel for XPath 2 supoprt (saxon)
				System.setProperty("javax.xml.xpath.XPathFactory:" + Configuration.xPathObjectModel, "net.sf.saxon.xpath.XPathFactoryImpl");
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}

}

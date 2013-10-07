package edu.arizona.sirls.etc.site.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.sirls.etc.site.server.rpc.MatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.db.ConnectionPool;

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
		if(event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				//init connection pool
				ConnectionPool connectionPool = ConnectionPool.getInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

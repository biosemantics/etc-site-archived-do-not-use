package edu.arizona.biosemantics.etcsite.server;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;


@SuppressWarnings("serial")
public class KaptchaServlet extends HttpServlet implements Servlet {
	
	private DAOManager daoManager;
	
	@Inject
	public KaptchaServlet(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	
	@Override
	public void init(ServletConfig conf) {
		try {
			super.init(conf);
		} catch (ServletException e) {
			String message = "Couldn't initialize servlet";
			log(message, e);
			log(LogLevel.ERROR, message, e);
		}
		ImageIO.setUseCache(false);
	}

	/** */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String url = req.getRequestURL().toString();
		String idStr = url.substring(url.lastIndexOf("/") + 1);
		int id = Integer.parseInt(idStr);
		BufferedImage image = daoManager.getCaptchaDAO().getImage(id);

		// Set to expire far in the past.
		resp.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		resp.setHeader("Pragma", "no-cache");

		// return a jpeg
		resp.setContentType("image/jpeg");

		ServletOutputStream out = null;
		try {
			out = resp.getOutputStream();
		} catch (IOException e) {
			String message = "Couldn't get output stream image";
			log(message, e);
			log(LogLevel.ERROR, message, e);
		}
		if(out != null) {
			try {
				ImageIO.write(image, "jpg", out);
			} catch (IOException e) {
				String message = "Couldn't write image";
				log(message, e);
				log(LogLevel.ERROR, message, e);
			}
		}
	}
}
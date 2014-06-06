package edu.arizona.biosemantics.etcsite.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class MyKaptchaServlet extends HttpServlet implements Servlet {
	/*
	 * private Properties props = new Properties();
	 * 
	 * private Producer kaptchaProducer = null;
	 * 
	 * private String sessionKeyValue = null;
	 * 
	 * private String sessionKeyDateValue = null;
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);

		// Switch off disk based caching.
		ImageIO.setUseCache(false);

		/*
		 * Enumeration<?> initParams = conf.getInitParameterNames(); while
		 * (initParams.hasMoreElements()) { String key = (String)
		 * initParams.nextElement(); String value = conf.getInitParameter(key);
		 * this.props.put(key, value); }
		 * 
		 * Config config = new Config(this.props); this.kaptchaProducer =
		 * config.getProducerImpl(); this.sessionKeyValue =
		 * config.getSessionKey(); this.sessionKeyDateValue =
		 * config.getSessionDate();
		 */
	}

	/** */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = req.getRequestURL().toString();
		String idStr = url.substring(url.lastIndexOf("/") + 1);
		int id = Integer.parseInt(idStr);
		String newURL = CaptchaManager.getInstance().getURLFor(id);
		
		File imageFile = new File(newURL);
		BufferedImage image = ImageIO.read(imageFile);

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

		/*
		 * // create the text for the image String capText =
		 * this.kaptchaProducer.createText();
		 * 
		 * // store the text in the session
		 * req.getSession().setAttribute(this.sessionKeyValue, capText);
		 * 
		 * // store the date in the session so that it can be compared //
		 * against to make sure someone hasn't taken too long to enter // their
		 * kaptcha req.getSession().setAttribute(this.sessionKeyDateValue, new
		 * Date());
		 * 
		 * // create the image with the text BufferedImage bi =
		 * this.kaptchaProducer.createImage(capText);
		 */

		ServletOutputStream out = resp.getOutputStream();
		ImageIO.write(image, "jpg", out);
	}
}
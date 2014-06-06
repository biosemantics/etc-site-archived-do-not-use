package edu.arizona.biosemantics.etcsite.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;


public class CaptchaManager {
	private static final int MINUTES_BEFORE_EXPIRE = 5;
	private static final int MINUTES_BETWEEN_SCANS = 5;
	
	private static CaptchaManager instance;
	
	private Producer captchaProducer;
	private ConcurrentHashMap<Integer, Captcha> captchas;	
	private static int NEXT_ID = 100;
	
	public CaptchaManager(){
		Config config = new Config(new Properties());
		captchaProducer = config.getProducerImpl();
		captchas = new ConcurrentHashMap<Integer, Captcha>();
		
		//clear all current files in captcha directory. 
		try {
			FileUtils.cleanDirectory(new File(Configuration.captcha_tempFileBase));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// schedule a task to automatically scan and delete old captchas. 
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				deleteExpiredCaptchas();
			}
		}, 
		MINUTES_BETWEEN_SCANS * 60 * 1000, //first scan happens in <MINUTES_BETWEEN_SCANS> minutes. 
		MINUTES_BETWEEN_SCANS * 60 * 1000); //subsequent scans happen every <MINUTES_BETWEEN_SCANS> minutes. 
	}
	
	public static CaptchaManager getInstance(){
		if (instance == null)
			instance = new CaptchaManager();
		return instance;
	}
	
	/**
	 * Generates a new captcha.
	 * @return the unique id of this captcha. 
	 */
	public int addCaptcha(){
		
		String solution = captchaProducer.createText();
		BufferedImage img = captchaProducer.createImage(solution);
		
		int id = NEXT_ID++;
		String filename = "captcha" + id + ".jpg";
		
		File outputFile = new File(Configuration.captcha_tempFileBase + File.separator + filename);
		
		//write the image to the output file. 
		try {
			//System.out.println("creating file " + outputFile.getAbsolutePath() + " with id " + id + " and solution " + solution);
			ImageIO.write(img, "jpg", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		captchas.put(id, new Captcha(outputFile.getAbsolutePath(), solution));
		return id;
	}
	
	public void deleteExpiredCaptchas(){
		ArrayList<Integer> expired = new ArrayList<Integer>();
		
		long now = new Date().getTime();
		for (Integer id : captchas.keySet()){
			Captcha captcha = captchas.get(id);
			if (now - captcha.getTimestamp().getTime() > MINUTES_BEFORE_EXPIRE * 60 * 1000 ){
				//System.out.println("Captcha " + id + " is expired. deleting.");
				expired.add(id);
			}
		}
		
		for (Integer id : expired){
			removeAndDelete(id);
		}
	}
	
	public String getURLFor(int id){
		Captcha captcha = captchas.get(id);
		if (captcha == null)
			return null;
		return captcha.getURL();
	}
	
	public boolean isValidSolution(int id, String solution){
		Captcha captcha = captchas.get(id);
		if (captcha == null)
			return false;
		
		boolean success = captcha.getSolution().equals(solution);
		removeAndDelete(id); //it's a one-try deal; delete this entry whether they got it right or wrong. 
		return success; 
	}
	
	private void removeAndDelete(int id){
		Captcha captcha = captchas.get(id);
		if (captcha == null)
			return;
		File file = new File(captcha.getURL());
		file.delete();
		captchas.remove(id);
	}
	
	private class Captcha{
		private String url;
		private String solution;
		private Date timestamp;
		
		public Captcha(String url, String solution){
			this.url = url;
			this.solution = solution;
			timestamp = new Date();
		}
		
		public String getURL(){
			return url;
		}
		public String getSolution(){
			return solution;
		}
		public Date getTimestamp(){
			return timestamp;
		}
		
		public String toString(){
			return "Captcha: solution=" + solution + " url=" + url + " timestamp=" + timestamp;
		}
	}
}

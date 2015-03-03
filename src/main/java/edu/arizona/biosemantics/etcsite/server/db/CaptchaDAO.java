package edu.arizona.biosemantics.etcsite.server.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.model.Captcha;

public class CaptchaDAO {

	private Producer captchaProducer;
	
	public CaptchaDAO() {
		Config captchaConfig = new Config(new Properties());
		captchaProducer = captchaConfig.getProducerImpl();
	}
	
	public Captcha get(int id) {
		Captcha captcha = null;
		try(Query query = new Query("SELECT * FROM etcsite_captchas WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				captcha = createCaptcha(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get captcha", e);
		}
		return captcha;
	}

	private Captcha createCaptcha(ResultSet result) throws SQLException {
		int id = result.getInt(1);
		String solution = result.getString(2);
		Date created = result.getTimestamp(3);
		return new Captcha(id, solution, created);
	}

	public void remove(int captchaId) {
		getCaptchaFile(captchaId).delete();
		try(Query query = new Query("DELETE FROM etcsite_captchas WHERE id = ?")) {
			query.setParameter(1, captchaId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't remove captcha", e);
		}
	}
	
	public void remove(Captcha captcha) {
		this.remove(captcha.getId());
	}
	
	public void cleanup() {
		try(Query query = new Query("SELECT * FROM etcsite_captchas")) {
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				int id = resultSet.getInt(1);
				remove(id);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't cleanup captchas in DB", e);
		}
		File dir = new File(Configuration.captcha_tempFileBase);
		if (!dir.exists())
			dir.mkdirs();
		try {
			FileUtils.cleanDirectory(dir);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't cleanup captchas in FS", e);
		}
	}

	public boolean isValidSolution(int captchaId, String solution) {
		Captcha captcha = get(captchaId);
		remove(captchaId);		
		if(captcha == null)
			return false;
		
		Date now = new Date();
		if (now.getTime() - captcha.getCreated().getTime() >= 5*60*1000) {
		    return false;
		}
		return captcha.getSolution().equals(solution);
	}

	public Captcha insert() {
		String solution = captchaProducer.createText();
		BufferedImage img = captchaProducer.createImage(solution);
		
		try(Query insertCaptcha = new Query("INSERT INTO etcsite_captchas (solution) VALUES (?)")) {
			insertCaptcha.setParameter(1, solution);
			insertCaptcha.execute();
			ResultSet generatedKeys = insertCaptcha.getGeneratedKeys();
			if(generatedKeys.next()) {
				int id = generatedKeys.getInt(1);
				File outputFile = getCaptchaFile(id);
				try {
					ImageIO.write(img, "jpg", outputFile);
				} catch(IOException e) {
					log(LogLevel.ERROR, "Couldn't write captcha to FS", e);
				}
				return get(id);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert captcha", e);
		}
		return null;
	}

	public BufferedImage getImage(int captchaId) {
		File imageFile = getCaptchaFile( captchaId);
		try {
			BufferedImage image = ImageIO.read(imageFile);
			return image;
		} catch(IOException e) {
			log(LogLevel.ERROR, "Couldn't read captcha image from FS", e);
			return null;
		}
	}
	
	private File getCaptchaFile(int id) {
		return new File(Configuration.captcha_tempFileBase + File.separator + "captcha" + id + ".jpg");
	}
}

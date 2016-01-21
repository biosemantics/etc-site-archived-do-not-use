package edu.arizona.biosemantics.etcsite.core.server;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.log.Logger;

public class Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);

	private static Properties properties;
	
	public static String secret;
	
	/** Database **/
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	public static String databaseHost;
	public static String databasePort;
	public static int database_minConnectionsPerPartition;
	public static int database_maxConnectionsPerPartition;
	public static int database_partitionCount;
	
	/** Charaparser **/
	public static String charaparser_databaseName;
	public static String charaparser_wordnet;
	
	/** File Management **/
	public static String fileBase;
	public static String profiles;
	public static String etcFiles;
	
	/** Captcha **/
	public static String captcha_tempFileBase;

	public static String googleClientId;
	public static String googleRedirectURI;
	public static String googleSecret;

	public static String emailSMTPServer;
	public static String emailSMTPPort;
	protected static String emailAddress;
	protected static String emailPassword;

	public static String passwordResetBody;
	public static String passwordResetSubject;
	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/core/config.properties"));
			
			secret = properties.getProperty("secret");
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			database_minConnectionsPerPartition = Integer.valueOf(properties.getProperty("database_minConnectionsPerPartition"));
			database_maxConnectionsPerPartition = Integer.valueOf(properties.getProperty("database_maxConnectionsPerPartition"));
			database_partitionCount = Integer.valueOf(properties.getProperty("database_partitionCount"));
			
			charaparser_databaseName = properties.getProperty("charaparser_databaseName");
			charaparser_wordnet = properties.getProperty("charaparser_wordnet");
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));;
			profiles = properties.getProperty("profiles").replaceAll("/", Matcher.quoteReplacement(File.separator));;
			etcFiles = properties.getProperty("etcFiles").replaceAll("/", Matcher.quoteReplacement(File.separator));;
			
			captcha_tempFileBase = properties.getProperty("captcha_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));				
			
			googleRedirectURI = properties.getProperty("google_redirect_URI");
			googleClientId = properties.getProperty("google_client_id");
			googleSecret = properties.getProperty("google_secret");

			emailSMTPServer = properties.getProperty("email_smtp_server");
			emailSMTPPort = properties.getProperty("email_smtp_port");
			emailAddress = properties.getProperty("email_address");
			emailPassword = properties.getProperty("email_password");

			passwordResetBody = properties.getProperty("password_reset_subject");
			passwordResetSubject = properties.getProperty("password_reset_body");
		
		} catch(Exception e) {
			logger.error("Couldn't read configuration", e);
		}
	}
	
	public static String asString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			return writer.writeValueAsString(properties);
		} catch (Exception e) {
			logger.error("Couldn't write configuration as string", e);
			return null;
		}
	}

}

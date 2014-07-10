package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class Configuration extends edu.arizona.biosemantics.etcsite.client.common.Configuration {

	/** Database **/
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	public static String databaseHost;
	public static String databasePort;
	
	/** Charaparser **/
	public static String charaparser_wordnet;
	public static String charaparser_perl;
	public static String charaparser_tempFileBase;
	public static String taxonDescriptionSchemaFile;
	public static String markedUpTaxonDescriptionSchemaFile;
	public static String otoLiteURL;
	public static String otoLiteReviewURL;
	
	/** Matrix Generation **/
	public static String matrixGeneration_tempFileBase;
	
	/** File Management **/
	public static String fileBase;
	public static String compressedFileBase;

	/** XPath object model **/
	public static String xPathObjectModel;
	
	/** Compress Command **/
	public static String compressCommand;

	/** Captcha **/
	public static String captcha_tempFileBase;
	
	/** Email Account **/
	public static String emailSMTPServer;
	public static String emailAddress;
	public static String emailPassword;
	
	/** Predefined Email text **/
	public static String passwordResetSubject;
	public static String passwordResetBody;
	public static String finishedSemanticMarkupLearnSubject;
	public static String finishedSemanticMarkupLearnBody;
	public static String finishedSemanticMarkupParseSubject;
	public static String finishedSemanticMarkupParseBody;
	public static String finishedMatrixgenerationGenerateSubject;
	public static String finishedMatrixgenerationGenerateBody;
	
	/** Sign in with Google **/
	public static String googleRedirectURI;
	public static String googleClientId;
	
	static {
		Logger.getLogger(DownloadServlet.class).debug("Init Configuration");
		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties properties = new Properties(); 
			properties.load(loader.getResourceAsStream("config.properties"));
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			
			charaparser_wordnet = properties.getProperty("charaparser_wordnet").replaceAll("/", Matcher.quoteReplacement(File.separator));
			charaparser_perl = properties.getProperty("charaparser_perl").replaceAll("/", Matcher.quoteReplacement(File.separator));	
			charaparser_tempFileBase = properties.getProperty("charaparser_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));				
			taxonDescriptionSchemaFile = properties.getProperty("taxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			markedUpTaxonDescriptionSchemaFile = properties.getProperty("markedUpTaxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			otoLiteURL = properties.getProperty("otoLiteURL");
			otoLiteReviewURL = properties.getProperty("otoLiteReviewURL");
			
			matrixGeneration_tempFileBase = properties.getProperty("matrixGeneration_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			compressedFileBase = properties.getProperty("compressedFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			xPathObjectModel = properties.getProperty("xPathObjectModel");
			
			compressCommand = properties.getProperty("compressCommand");
			
			captcha_tempFileBase = properties.getProperty("captcha_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			emailSMTPServer = properties.getProperty("email_smtp_server");
			emailAddress = properties.getProperty("email_address");
			emailPassword = properties.getProperty("email_password");
			
			passwordResetSubject = properties.getProperty("password_reset_subject");
			passwordResetBody = properties.getProperty("password_reset_body");
			finishedSemanticMarkupLearnSubject = properties.getProperty("finished_semantic_markup_learn_subject");
			finishedSemanticMarkupLearnBody = properties.getProperty("finished_semantic_markup_learn_body");
			finishedSemanticMarkupParseSubject = properties.getProperty("finished_semantic_markup_parse_subject");
			finishedSemanticMarkupParseBody = properties.getProperty("finished_semantic_markup_parse_body");
			finishedMatrixgenerationGenerateSubject = properties.getProperty("finished_martrix_generation_genreate_subject"); 
			finishedMatrixgenerationGenerateBody = properties.getProperty("finished_matrix_generation_generate_body");
			
			googleRedirectURI = properties.getProperty("google_redirect_URI");
			googleClientId = properties.getProperty("google_client_id");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

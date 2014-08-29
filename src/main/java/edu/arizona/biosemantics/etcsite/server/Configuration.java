package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;

public class Configuration extends edu.arizona.biosemantics.etcsite.client.common.Configuration {

	public static String deploymentUrl;
	public static String classpath;
	
	/** Files **/
	public static String targetNamespace;
	public static String taxonDescriptionSchemaFileWeb;
	public static String markedUpTaxonDescriptionSchemaFileWeb;
	
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
	public static int maxActiveSemanticMarkup;
	public static String charaparser_xms;
	public static String charaparser_xmx;
	
	/** Matrix Generation **/
	public static String matrixGeneration_tempFileBase;
	public static int maxActiveMatrixGeneration;
	public static String matrixgeneration_xms;
	public static String matrixgeneration_xmx;
	
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
	public static String emailSMTPPort;
	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties properties = new Properties(); 
			properties.load(loader.getResourceAsStream("config.properties"));
			
			deploymentUrl = properties.getProperty("deploymentUrl");
			classpath = properties.getProperty("classpath");
			
			targetNamespace = properties.getProperty("targetNamespace");
			taxonDescriptionSchemaFileWeb = properties.getProperty("taxonDescriptionSchemaFileWeb");
			markedUpTaxonDescriptionSchemaFileWeb = properties.getProperty("markedUpTaxonDescriptionSchemaFileWeb");
			
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
			maxActiveSemanticMarkup = Integer.parseInt(properties.getProperty("maxActiveSemanticMarkup"));
			charaparser_xms = properties.getProperty("charaparser_xms");
			charaparser_xmx = properties.getProperty("charaparser_xmx");
			
			matrixGeneration_tempFileBase = properties.getProperty("matrixGeneration_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			maxActiveMatrixGeneration = Integer.parseInt(properties.getProperty("maxActiveMatrixGeneration"));
			matrixgeneration_xms = properties.getProperty("matrixgeneration_xms");
			matrixgeneration_xmx = properties.getProperty("matrixgeneration_xmx");
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			compressedFileBase = properties.getProperty("compressedFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			xPathObjectModel = properties.getProperty("xPathObjectModel");
			
			compressCommand = properties.getProperty("compressCommand");
			
			captcha_tempFileBase = properties.getProperty("captcha_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			emailSMTPServer = properties.getProperty("email_smtp_server");
			emailSMTPPort = properties.getProperty("email_smtp_port");
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

package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.log.Logger;

public class Configuration extends edu.arizona.biosemantics.etcsite.shared.Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);

	/** Java Setup **/
	public static String classpath;
	
	/** Files **/
	public static String targetNamespace;
	public static Set<String> taxonDescriptionSchemaFileWeb;
	public static Set<String> markedUpTaxonDescriptionSchemaFileWeb;
	
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
	public static String charaparser_perl;
	public static String charaparser_tempFileBase;
	public static String taxonDescriptionSchemaFile;
	public static String markedUpTaxonDescriptionSchemaFile;
	public static int maxActiveSemanticMarkup;
	public static String charaparser_xms;
	public static String charaparser_xmx;
	public static String charaparser_ontologies;
	public static int semanticMarkup_numberOfWordsToWarnUser;
	public static int semanticMarkup_learnStep_maxRunningTimeMinutes;
	public static int semanticMarkup_parseStep_maxRunningTimeMinutes;
	public static String micropie_classpath;
	public static String micropie_models;
	
	/** Matrix Generation **/
	public static String matrixGeneration_tempFileBase;
	public static int maxActiveMatrixGeneration;
	public static String matrixgeneration_xms;
	public static String matrixgeneration_xmx;
	public static String matrixgeneration_classpath;
	public static int matrixGeneration_maxRunningTimeMinutes;
	public static String matrixReview_tempFileBase;
	
	/** Taxonomy Comparison **/
	public static int maxActiveTaxonomyComparison;
	public static int taxonomyComparisonTask_maxRunningTimeMinutes;
	public static String taxonomyComparison_tempFileBase;
	public static String taxonomyComparison_xms;
	public static String taxonomyComparison_xmx;
	
	/** File Management **/
	public static String fileBase;
	public static String profiles;
	public static String compressedFileBase;
	public static String tempFiles;
	public static String etcFiles;

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
	public static String failedSemanticMarkupParseSubject;
	public static String failedSemanticMarkupParseBody;
	public static String finishedMatrixgenerationGenerateSubject;
	public static String finishedMatrixgenerationGenerateBody;
	
	/** Sign in with Google **/
	public static String googleRedirectURI;
	public static String googleClientId;
	public static String googleSecret;
	public static String emailSMTPPort;
	private static Properties properties;
	
	/** OTO 2 **/
	public static String oto2Url;
	
	/** Ontologize **/
	public static String ontologizeUrl;

	public static String otoUrl;
	public static String otoSecret;
	public static String secret;

	public static String publicFolder;

	public static Set<String> illegalOrderModifiers;


	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/config.properties"));
			
			ontologizeUrl = properties.getProperty("ontologizeUrl");
			oto2Url = properties.getProperty("oto2Url");
			otoUrl = properties.getProperty("otoUrl");
			otoSecret = properties.getProperty("otoSecret");
			secret = properties.getProperty("secret");
			classpath = properties.getProperty("classpath");
			
			targetNamespace = properties.getProperty("targetNamespace");
			taxonDescriptionSchemaFileWeb = new HashSet<String>(Arrays.asList(properties.getProperty("taxonDescriptionSchemaFileWeb").split(";")));
			markedUpTaxonDescriptionSchemaFileWeb = new HashSet<String>(Arrays.asList(properties.getProperty("markedUpTaxonDescriptionSchemaFileWeb").split(";")));
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			database_minConnectionsPerPartition = Integer.valueOf(properties.getProperty("database_minConnectionsPerPartition"));
			database_maxConnectionsPerPartition = Integer.valueOf(properties.getProperty("database_maxConnectionsPerPartition"));
			database_partitionCount = Integer.valueOf(properties.getProperty("database_partitionCount"));
			
			charaparser_databaseName = properties.getProperty("charaparser_databaseName");
			charaparser_wordnet = properties.getProperty("charaparser_wordnet").replaceAll("/", Matcher.quoteReplacement(File.separator));
			charaparser_perl = properties.getProperty("charaparser_perl").replaceAll("/", Matcher.quoteReplacement(File.separator));	
			charaparser_tempFileBase = properties.getProperty("charaparser_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));				
			taxonDescriptionSchemaFile = properties.getProperty("taxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			markedUpTaxonDescriptionSchemaFile = properties.getProperty("markedUpTaxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			maxActiveSemanticMarkup = Integer.parseInt(properties.getProperty("maxActiveSemanticMarkup"));
			charaparser_xms = properties.getProperty("charaparser_xms");
			charaparser_xmx = properties.getProperty("charaparser_xmx");
			charaparser_ontologies = properties.getProperty("charaparser_ontologies");
			semanticMarkup_numberOfWordsToWarnUser = Integer.valueOf(properties.getProperty("semanticMarkup_numberOfWordsToWarnUser"));
			semanticMarkup_learnStep_maxRunningTimeMinutes = Integer.valueOf(properties.getProperty("semanticMarkup_learnStep_maxRunningTimeMinutes"));
			semanticMarkup_parseStep_maxRunningTimeMinutes = Integer.valueOf(properties.getProperty("semanticMarkup_parseStep_maxRunningTimeMinutes"));
			micropie_classpath = properties.getProperty("micropie_classpath");
			micropie_models = properties.getProperty("micropie_models");
			
			matrixGeneration_tempFileBase = properties.getProperty("matrixGeneration_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			maxActiveMatrixGeneration = Integer.parseInt(properties.getProperty("maxActiveMatrixGeneration"));
			matrixgeneration_xms = properties.getProperty("matrixgeneration_xms");
			matrixgeneration_xmx = properties.getProperty("matrixgeneration_xmx");
			matrixgeneration_classpath = properties.getProperty("matrixgeneration_classpath");
			matrixGeneration_maxRunningTimeMinutes = Integer.valueOf(properties.getProperty("matrixGeneration_maxRunningTimeMinutes"));
			matrixReview_tempFileBase = properties.getProperty("matrixReview_tempFileBase");
			taxonomyComparison_xms = properties.getProperty("taxonomyComparison_xms");
			taxonomyComparison_xmx = properties.getProperty("taxonomyComparison_xmx");
			taxonomyComparison_tempFileBase = properties.getProperty("taxonomyComparison_tempFileBase");
			
			maxActiveTaxonomyComparison = Integer.parseInt(properties.getProperty("maxActiveTaxonomyComparison"));
			taxonomyComparisonTask_maxRunningTimeMinutes = Integer.parseInt(properties.getProperty("taxonomyComparisonTask_maxRunningTimeMinutes"));
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			profiles = properties.getProperty("profiles").replaceAll("/", Matcher.quoteReplacement(File.separator));
			compressedFileBase = properties.getProperty("compressedFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			etcFiles= properties.getProperty("etcFiles").replaceAll("/", Matcher.quoteReplacement(File.separator));
			publicFolder = properties.getProperty("publicFolder").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
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
			failedSemanticMarkupParseSubject = properties.getProperty("failed_semantic_markup_parse_subject");
			failedSemanticMarkupParseBody = properties.getProperty("failed_semantic_markup_parse_body");
			finishedMatrixgenerationGenerateSubject = properties.getProperty("finished_martrix_generation_genreate_subject"); 
			finishedMatrixgenerationGenerateBody = properties.getProperty("finished_matrix_generation_generate_body");
			
			googleRedirectURI = properties.getProperty("google_redirect_URI");
			googleClientId = properties.getProperty("google_client_id");
			googleSecret = properties.getProperty("google_secret");
			
			illegalOrderModifiers = new HashSet<String>(Arrays.asList(properties.getProperty("illegalOrderModifiers").split(",")));	
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

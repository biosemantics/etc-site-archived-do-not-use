package edu.arizona.sirls.etc.site.server;

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;

public class Configuration extends edu.arizona.sirls.etc.site.client.Configuration {

	/** Database **/
	public static String databaseName;
	public static String databaseUser;
	public static String databasePassword;
	public static String databaseHost;
	public static String databasePort;
	
	/** Charaparser **/
	public static String charaparser_resources;
	public static String charaparser_src;
	public static String charaparser_tempFileBase;
	public static String taxonDescriptionSchemaFile;
	public static String markedUpTaxonDescriptionSchemaFile;
	public static String otoLiteURL;
	
	/** Matrix Generation **/
	public static String matrixGeneration_tempFileBase;
	
	/** File Management **/
	public static String fileBase;
	public static String compressedFileBase;

	/** XPath object model **/
	public static String xPathObjectModel;

	static {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties properties = new Properties(); 
			properties.load(loader.getResourceAsStream("config.properties"));
			
			databaseName = properties.getProperty("databaseName");
			databaseUser = properties.getProperty("databaseUser");
			databasePassword = properties.getProperty("databasePassword");
			databaseHost = properties.getProperty("databaseHost");
			databasePort = properties.getProperty("databasePort");
			
			charaparser_resources = properties.getProperty("charaparser_resources");
			charaparser_resources = charaparser_resources.replaceAll("/", Matcher.quoteReplacement(File.separator));	
			charaparser_src = properties.getProperty("charaparser_src").replaceAll("/", Matcher.quoteReplacement(File.separator));	
			charaparser_tempFileBase = properties.getProperty("charaparser_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));				
			taxonDescriptionSchemaFile = properties.getProperty("taxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			markedUpTaxonDescriptionSchemaFile = properties.getProperty("markedUpTaxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			otoLiteURL = properties.getProperty("otoLiteURL");
			
			matrixGeneration_tempFileBase = properties.getProperty("matrixGeneration_tempFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			compressedFileBase = properties.getProperty("compressedFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			xPathObjectModel = properties.getProperty("xPathObjectModel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

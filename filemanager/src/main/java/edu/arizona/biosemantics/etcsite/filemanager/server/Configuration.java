package edu.arizona.biosemantics.etcsite.filemanager.server;

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

	public static String fileBase;
	public static String publicFolder;
	public static String compressedFileBase;
	public static String taxonDescriptionSchemaFile;
	public static String markedUpTaxonDescriptionSchemaFile;
	public static String targetNamespace;
	public static Set<String> taxonDescriptionSchemaFileWeb;
	public static Set<String> markedUpTaxonDescriptionSchemaFileWeb;
	public static String compressCommand;
	public static String xPathObjectModel;
	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/filemanager/config.properties"));
			
			fileBase = properties.getProperty("fileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			compressedFileBase = properties.getProperty("compressedFileBase").replaceAll("/", Matcher.quoteReplacement(File.separator));
			publicFolder = properties.getProperty("publicFolder").replaceAll("/", Matcher.quoteReplacement(File.separator));

			targetNamespace = properties.getProperty("targetNamespace");
			taxonDescriptionSchemaFileWeb = new HashSet<String>(Arrays.asList(properties.getProperty("taxonDescriptionSchemaFileWeb").split(";")));
			markedUpTaxonDescriptionSchemaFileWeb = new HashSet<String>(Arrays.asList(properties.getProperty("markedUpTaxonDescriptionSchemaFileWeb").split(";")));
			
			taxonDescriptionSchemaFile = properties.getProperty("taxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			markedUpTaxonDescriptionSchemaFile = properties.getProperty("markedUpTaxonDescriptionSchemaFile").replaceAll("/", Matcher.quoteReplacement(File.separator));
			
			xPathObjectModel = properties.getProperty("xPathObjectModel");
			compressCommand = properties.getProperty("compressCommand");
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
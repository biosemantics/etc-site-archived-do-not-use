package edu.arizona.biosemantics.etcsite.filemanager.server;

import java.util.Properties;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import edu.arizona.biosemantics.common.log.Logger;

public class Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);
	private static Properties properties;
	
	public static String publicFolder;
	public static String compressedFileBase;
	public static String fileBase;
	public static String taxonDescriptionSchemaFile;
	public static String markedUpTaxonDescriptionSchemaFile;
	public static String targetNamespace;
	public static Set<String> taxonDescriptionSchemaFileWeb;
	public static Set<String> markedUpTaxonDescriptionSchemaFileWeb;
	public static String compressCommand;
	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/filemanager/config.properties"));
			
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

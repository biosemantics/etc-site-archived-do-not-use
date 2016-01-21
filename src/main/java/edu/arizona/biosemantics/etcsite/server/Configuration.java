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

public class Configuration {

	private final static Logger logger = Logger.getLogger(Configuration.class);

	/** Java Setup **/
	public static String classpath;
		
	/** Charaparser **/
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
	
	/** Predefined Email text **/
	public static String finishedSemanticMarkupLearnSubject;
	public static String finishedSemanticMarkupLearnBody;
	public static String finishedSemanticMarkupParseSubject;
	public static String finishedSemanticMarkupParseBody;
	public static String finishedMatrixgenerationGenerateSubject;
	public static String finishedMatrixgenerationGenerateBody;
	
	private static Properties properties;
	
	/** OTO 2 **/
	public static String oto2Url;
	
	/** Ontologize **/
	public static String ontologizeUrl;

	public static String otoUrl;
	public static String otoSecret;
	
	static {		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			properties = new Properties(); 
			properties.load(loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/config.properties"));
			
			ontologizeUrl = properties.getProperty("ontologizeUrl");
			oto2Url = properties.getProperty("oto2Url");
			otoUrl = properties.getProperty("otoUrl");
			otoSecret = properties.getProperty("otoSecret");
			classpath = properties.getProperty("classpath");
			
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
			
			finishedSemanticMarkupLearnSubject = properties.getProperty("finished_semantic_markup_learn_subject");
			finishedSemanticMarkupLearnBody = properties.getProperty("finished_semantic_markup_learn_body");
			finishedSemanticMarkupParseSubject = properties.getProperty("finished_semantic_markup_parse_subject");
			finishedSemanticMarkupParseBody = properties.getProperty("finished_semantic_markup_parse_body");
			finishedMatrixgenerationGenerateSubject = properties.getProperty("finished_martrix_generation_genreate_subject"); 
			finishedMatrixgenerationGenerateBody = properties.getProperty("finished_matrix_generation_generate_body");				
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

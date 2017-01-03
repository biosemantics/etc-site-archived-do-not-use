package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.semanticmarkup.ETCMarkupMain;

public class ExtraJvmParse extends ExtraJvmCallable<ParseResult> implements CharaparserParse {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				ETCMarkupMain.main(args);
			} catch (Throwable t) {
				System.exit(-1);
			}
		}
		
	}
	
	private String config;
	private String input;
	private String tablePrefix;
	private String source;
	private String operator;
	private boolean useEmptyGlossary;	

	public ExtraJvmParse(String config, boolean useEmptyGlossary, String input, String tablePrefix,
			String source, String operator) {
		super();
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.useEmptyGlossary = useEmptyGlossary;
		
		this.setArgs(createArgs());
		if(!Configuration.charaparser_xms.isEmpty()) 
			this.setXms(Configuration.charaparser_xms);
		if(!Configuration.charaparser_xmx.isEmpty()) 
			this.setXmx(Configuration.charaparser_xmx);
		
		//could be reduced to only libraries relevant to semantic-markup
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(MainWrapper.class);
	}

	private String[] createArgs() {
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		//String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		
		List<String> argList = new LinkedList<String>();
		addArg(argList, "f", source);
		addArg(argList, "g", operator);
		addArg(argList, "c", config);
		addArg(argList, "i", input);
		addArg(argList, "z", tablePrefix);
		if(useEmptyGlossary)
			addArg(argList, "x");
		
		String[] args = argList.toArray(new String[argList.size()]);
		
		/*
		String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, 
				"-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL}; */
		
		//System.out.println();
		//for(String arg : args) {
		//	System.out.print(arg + " ");
		//}
		//System.out.println();
		
		return args;
		//File outputFile = new File("workspace" + File.separator + tablePrefix + File.separator + "out");
		//for(File outFile : outputFile.listFiles()) {
		//	result.getOutputFiles().add(outFile);
		//}
		
		/**
		 * This will only stay until charaparser outputs data with namespace already set
		 */
		/*XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
		String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + tablePrefix + File.separator + "out";
		for(File child : new File(charaParserOutputDirectory).listFiles()) {
			if(child.isFile() && !child.getName().equals("config.txt")) {
				xmlNamespaceManager.setXmlSchema(child, FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION);
			}
		}*/
	}
	
	private void addArg(List<String> argList, String arg) {
		argList.add("-" + arg);
	}

	private void addArg(List<String> argList, String arg, String value) {
		if(value != null && !value.isEmpty()) {
			argList.add("-" + arg);
			argList.add(value);
		}
	}

	@Override
	public ParseResult createReturn() throws SemanticMarkupException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Semantic Markup Parse failed.");
			throw new SemanticMarkupException();
		}
		ParseResult result = new ParseResult(new HashSet<File>());
		return result;
	}

}

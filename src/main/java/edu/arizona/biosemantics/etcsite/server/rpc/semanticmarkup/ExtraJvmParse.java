package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import edu.arizona.biosemantics.semanticmarkup.ETCMarkupMain;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;

public class ExtraJvmParse extends ExtraJvmCallable<ParseResult> implements Parse {

	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;
	private String source;
	private String operator;
	private String bioportalUserId;
	private String bioportalAPIKey;	

	public ExtraJvmParse(AuthenticationToken authenticationToken, String config, String input, String tablePrefix,
			String source, String operator, String bioportalUserId, String bioportalAPIKey) {
		super();
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		
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
		this.setMainClass(ETCMarkupMain.class);
	}

	private String[] createArgs() {
		String databaseName = Configuration.databaseName;
		String databaseUser = Configuration.databaseUser;
		String databasePassword = Configuration.databasePassword;
		String databaseHost = Configuration.databaseHost;
		String databasePort = Configuration.databasePort;
		String workspace = Configuration.charaparser_tempFileBase;
		String wordnet = Configuration.charaparser_wordnet;
		String perl = Configuration.charaparser_perl;
		String otoLiteURL = Configuration.deploymentUrl;
		String debugFile = workspace + File.separator + tablePrefix + File.separator + "debug.log";
		String errorFile = workspace + File.separator + tablePrefix + File.separator + "error.log";
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		//String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		
		List<String> argList = new LinkedList<String>();
		addArg(argList, "a", workspace);
		addArg(argList, "f", source);
		addArg(argList, "g", operator);
		addArg(argList, "j", bioportalUserId);
		addArg(argList, "k", bioportalAPIKey);
		addArg(argList, "b", debugFile);
		addArg(argList, "e", errorFile);
		addArg(argList, "c", config);
		addArg(argList, "w", wordnet);
		addArg(argList, "l", perl);
		addArg(argList, "n", databaseHost);
		addArg(argList, "p", databasePort);
		addArg(argList, "d", databaseName);
		addArg(argList, "u", databaseUser);
		addArg(argList, "s", databasePassword);
		addArg(argList, "i", input);
		addArg(argList, "z", tablePrefix);
		argList.add("-y");
		addArg(argList, "o", otoLiteURL);
		
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
	
	private void addArg(List<String> argList, String arg, String value) {
		if(value != null && !value.isEmpty()) {
			argList.add("-" + arg);
			argList.add(value);
		}
	}

	@Override
	public ParseResult createReturn() {
		ParseResult result = new ParseResult(new HashSet<File>());
		return result;
	}


}

package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.semanticmarkup.ETCMarkupMain;

public class InJvmParse implements Parse {

	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;
	private String source;
	private String operator;
	private String bioportalUserId;
	private String bioportalAPIKey;	

	public InJvmParse(AuthenticationToken authenticationToken, String config, String input, String tablePrefix,
			String source, String operator, String bioportalUserId, String bioportalAPIKey) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
	}
	
	@Override
	public ParseResult call() throws Exception {
		ParseResult result = new ParseResult(new HashSet<File>());
		String databaseName = Configuration.databaseName;
		String databaseUser = Configuration.databaseUser;
		String databasePassword = Configuration.databasePassword;
		String databaseHost = Configuration.databaseHost;
		String databasePort = Configuration.databasePort;
		String workspace = Configuration.charaparser_tempFileBase;
		String wordnet = Configuration.charaparser_wordnet;
		String perl = Configuration.charaparser_perl;
		String otoLiteURL = Configuration.otoLiteURL;
		String debugFile = workspace + File.separator + tablePrefix + File.separator + "debug.log";
		String errorFile = workspace + File.separator + tablePrefix + File.separator + "error.log";
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		//String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		
		String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, "-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL};
		
		//System.out.println();
		//for(String arg : args) {
		//	System.out.print(arg + " ");
		//}
		//System.out.println();
		
		ETCMarkupMain.main(args);
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
		return result;
	}

	@Override
	public void destroy() {}

}

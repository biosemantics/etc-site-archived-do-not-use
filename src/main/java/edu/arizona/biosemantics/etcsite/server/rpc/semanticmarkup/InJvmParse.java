package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
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
	private boolean executedSuccessfully = false;
	private boolean useEmptyGlossary;

	public InJvmParse(AuthenticationToken authenticationToken, String config, boolean useEmptyGlossary, String input, String tablePrefix,
			String source, String operator, String bioportalUserId, String bioportalAPIKey) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		this.useEmptyGlossary = useEmptyGlossary;
	}
	
	@Override
	public ParseResult call() throws SemanticMarkupException {
		ParseResult result = new ParseResult(new HashSet<File>());
		String databaseName = Configuration.charaparser_databaseName;
		String databaseUser = Configuration.databaseUser;
		String databasePassword = Configuration.databasePassword;
		String databaseHost = Configuration.databaseHost;
		String databasePort = Configuration.databasePort;
		String workspace = Configuration.charaparser_tempFileBase;
		String wordnet = Configuration.charaparser_wordnet;
		String perl = Configuration.charaparser_perl;
		String otoLiteURL = Configuration.oto2Url;
		String debugFile = workspace + File.separator + tablePrefix + File.separator + "debug.log";
		String errorFile = workspace + File.separator + tablePrefix + File.separator + "error.log";
		String ontologies = Configuration.charaparser_ontologies;
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		//String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		
		String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, "-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL, "-q", ontologies};
		if(useEmptyGlossary) {
			List<String> argList = new ArrayList<String>(Arrays.asList(args));
			argList.add("-x");
			args = argList.toArray(new String[argList.size()]);
		}
		
		//System.out.println();
		//for(String arg : args) {
		//	System.out.print(arg + " ");
		//}
		//System.out.println();
		
		try {
			ETCMarkupMain.main(args);
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Semantic Markup Parse failed with exception.", e);
			executedSuccessfully = false;
			throw new SemanticMarkupException();
		}
		
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

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}


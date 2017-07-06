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

public class InJvmParse implements CharaparserParse {

	private String config;
	private String input;
	private String tablePrefix;
	private String source;
	private String operator;
	private boolean executedSuccessfully = false;
	private boolean useEmptyGlossary;

	public InJvmParse(String config, boolean useEmptyGlossary, String input, String tablePrefix,
			String source, String operator) {
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.useEmptyGlossary = useEmptyGlossary;
	}
	
	@Override
	public ParseResult call() throws SemanticMarkupException {
		ParseResult result = new ParseResult(new HashSet<File>());
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		//String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		
		String[] args = new String[] { "-f", source, "-g", operator, "-c", config,"-i", input, "-z" , tablePrefix };
		if(useEmptyGlossary) {
			List<String> argList = new ArrayList<String>(Arrays.asList(args));
			argList.add("-x");
			args = argList.toArray(new String[argList.size()]);
		}
		
		System.out.println();
		for(String arg : args) {
			System.out.print(arg + " ");
		}
		System.out.println();
		
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


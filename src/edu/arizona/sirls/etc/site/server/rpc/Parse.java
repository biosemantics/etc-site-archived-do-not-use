package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;

import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;

import semanticMarkup.MarkupMain;

public class Parse implements IParse {

	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;
	private String source;
	private String user;
	private String bioportalUserId;
	private String bioportalAPIKey;	

	public Parse(AuthenticationToken authenticationToken, String config, String input, String tablePrefix,
			String source, String user, String bioportalUserId, String bioportalAPIKey) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.user = user;
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
		String resources = Configuration.charaparser_resources;
		String src = Configuration.charaparser_src;
		String debugFile = workspace + File.separator + tablePrefix + File.separator + "debug.log";
		String errorFile = workspace + File.separator + tablePrefix + File.separator + "error.log";
		String[] args = new String[] { "-a", workspace, "-f", source, "-g", user, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, "-e", errorFile, "-c", config, "-r", resources, "-l", src,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y" };
		
		System.out.println();
		for(String arg : args) {
			System.out.print(arg + " ");
		}
		System.out.println();
		
		MarkupMain.main(args);
		//File outputFile = new File("workspace" + File.separator + tablePrefix + File.separator + "out");
		//for(File outFile : outputFile.listFiles()) {
		//	result.getOutputFiles().add(outFile);
		//}
		return result;
	}

}

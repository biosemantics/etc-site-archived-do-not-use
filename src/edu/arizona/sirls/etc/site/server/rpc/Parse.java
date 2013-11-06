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
	private String debugFile;
	private String errorFile;
	private String source;
	private String user;
	private String bioportalUserId;
	private String bioportalAPIKey;	

	public Parse(AuthenticationToken authenticationToken, String config, String input, String tablePrefix, String debugFile, String errorFile,
			String source, String user, String bioportalUserId, String bioportalAPIKey) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.debugFile = debugFile;
		this.errorFile = errorFile;
		this.source = source;
		this.user = user;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
	}
	
	@Override
	public ParseResult call() throws Exception {
		ParseResult result = new ParseResult(new HashSet<File>());
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties(); 
		properties.load(loader.getResourceAsStream("config.properties"));
		String databaseName = properties.getProperty("databaseName");
		String databaseUser = properties.getProperty("databaseUser");
		String databasePassword = properties.getProperty("databasePassword");
		String databaseHost = "localhost";
		String databasePort = "3306";
		String resources = "resources" + File.separator + "charaparser" + File.separator + "resources";
		String src = "resources" + File.separator + "charaparser" + File.separator + "src";
		String[] args = new String[] { "-f", source, "-g", user, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, "-e", errorFile, "-c", config, "-r", resources, "-l", src,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix };
		MarkupMain.main(args);
		//File outputFile = new File("workspace" + File.separator + tablePrefix + File.separator + "out");
		//for(File outFile : outputFile.listFiles()) {
		//	result.getOutputFiles().add(outFile);
		//}
		return result;
	}

}

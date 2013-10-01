package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;

import com.google.gwt.thirdparty.guava.common.io.Files;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;

import semanticMarkup.MarkupMain;

public class Parse implements IParse {

	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;	

	public Parse(AuthenticationToken authenticationToken, String config, String input, String tablePrefix) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
	}
	
	@Override
	public ParseResult call() throws Exception {
		ParseResult result = new ParseResult(new HashSet<File>());
		String fileSystemInput = Configuration.fileBase +  File.separator + authenticationToken.getUsername() + File.separator + input;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties(); 
		properties.load(loader.getResourceAsStream("config.properties"));
		String databaseName = properties.getProperty("databaseName");
		String databaseUser = properties.getProperty("databaseUser");
		String databasePassword = properties.getProperty("databasePassword");
		String databaseHost = "localhost";
		String databasePort = "3306";
		tablePrefix = "FNA_v7";
		String[] args = new String[] { "-c", config, "-r", "resources" + File.separator + "charaparser", 
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", fileSystemInput, "-z" , tablePrefix };
		MarkupMain.main(args);
		File outputFile = new File("workspace" + File.separator + tablePrefix + File.separator + "out");
		for(File outFile : outputFile.listFiles()) {
			result.getOutputFiles().add(outFile);
		}
		return result;
	}

}

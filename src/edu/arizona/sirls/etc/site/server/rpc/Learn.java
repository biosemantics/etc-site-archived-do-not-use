package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.util.Properties;

import semanticMarkup.LearnMain;
import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.db.DatasetPrefix;
import edu.arizona.sirls.etc.site.shared.rpc.db.DatasetPrefixDAO;

public class Learn implements ILearn {

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

	public Learn(AuthenticationToken authenticationToken, String config, String input, String tablePrefix, String debugFile, String errorFile,
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
	public LearnResult call() throws Exception {
		String fileSystemInput = Configuration.fileBase +  File.separator + authenticationToken.getUsername() + File.separator + input;
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
				"-s", databasePassword, "-i", fileSystemInput, "-z" , tablePrefix };
		LearnMain.main(args);
		DatasetPrefix datasetPrefix = DatasetPrefixDAO.getInstance().getDatasetPrefix(tablePrefix);
		LearnResult result = new LearnResult(datasetPrefix.getOtoId());
		return result;
	}

}

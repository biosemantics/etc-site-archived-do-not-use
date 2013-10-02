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

	public Learn(AuthenticationToken authenticationToken, String config, String input, String tablePrefix) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
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
		String[] args = new String[] { "-c", config, "-r", resources, "-l", src,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", fileSystemInput, "-z" , tablePrefix };
		LearnMain.main(args);
		DatasetPrefix datasetPrefix = DatasetPrefixDAO.getInstance().getDatasetPrefix(tablePrefix);
		LearnResult result = new LearnResult(datasetPrefix.getOtoId());
		return result;
	}

}

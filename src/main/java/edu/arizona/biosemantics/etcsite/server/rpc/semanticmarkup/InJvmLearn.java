package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Task.FailHandler;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.semanticmarkup.ETCLearnMain;

public class InJvmLearn implements Learn {
	
	private DAOManager daoManager = new DAOManager();
	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;
	private String source;
	private String operator;
	private String bioportalUserId;
	private String bioportalAPIKey;
	private IFileService fileService = new FileService();
	private boolean executedSuccessfully = false;

	public InJvmLearn(AuthenticationToken authenticationToken, String config, String input, String tablePrefix,
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
	public LearnResult call() throws Exception {
		String databaseName = Configuration.charaparser_databaseName;
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
		
		fileService.createDirectory(new AdminAuthenticationToken(), workspace, tablePrefix, false);
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		/*fileService.createDirectory(new AdminAuthenticationToken(), workspace + File.separator + tablePrefix, "in", false);
		String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		fileService.copyFiles(new AdminAuthenticationToken(), input,  newInput);
		
		XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
		for(File child : new File(newInput).listFiles()) {
			xmlNamespaceManager.removeXmlSchema(child);
		}*/
		String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, "-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL};
		try {
			ETCLearnMain.main(args);
			DatasetPrefix datasetPrefix = daoManager.getDatasetPrefixDAO().getDatasetPrefix(tablePrefix);
			LearnResult result = new LearnResult(datasetPrefix.getOtoUploadId(), datasetPrefix.getOtoSecret());	
			executedSuccessfully = true;
			return result;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Semantic Markup Learn failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			handleFail();
		}
		return null;
	}
	
	protected void handleFail() {
		for(FailHandler failHandler : failHandlers) {
			failHandler.onFail();
		}
	}

	@Override
	public void destroy() {}

	private Set<FailHandler> failHandlers = new HashSet<FailHandler>();
	
	@Override
	public void addFailHandler(FailHandler handler) {
		failHandlers.add(handler);
	}
	
	@Override
	public void removeFailHandler(FailHandler handler) {
		failHandlers.remove(handler);
	}

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}

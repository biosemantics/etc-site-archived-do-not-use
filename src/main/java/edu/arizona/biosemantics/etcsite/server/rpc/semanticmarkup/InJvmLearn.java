package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
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
	public LearnResult call() throws SemanticMarkupException {
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
		
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), workspace, tablePrefix, false);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e1) {
			throw new SemanticMarkupException(null);
		}
		
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
			//throw new SemanticMarkupException(null); //test failing process
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Semantic Markup Learn failed with exception.", e);
			executedSuccessfully = false;
			throw new SemanticMarkupException(null);
		}
	}
	
	@Override
	public void destroy() {}

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}

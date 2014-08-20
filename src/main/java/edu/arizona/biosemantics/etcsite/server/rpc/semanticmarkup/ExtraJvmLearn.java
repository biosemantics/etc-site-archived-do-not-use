package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.semanticmarkup.ETCLearnMain;
import edu.arizona.biosemantics.semanticmarkup.ETCMarkupMain;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DatasetPrefixDAO;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;

public class ExtraJvmLearn extends ExtraJvmCallable<LearnResult> implements Learn {

	private String config;
	private String input;
	private String tablePrefix;
	private AuthenticationToken authenticationToken;
	private String source;
	private String operator;
	private String bioportalUserId;
	private String bioportalAPIKey;
	private IFileService fileService = new FileService();

	public ExtraJvmLearn(AuthenticationToken authenticationToken, String config, String input, String tablePrefix,
			String source, String operator, String bioportalUserId, String bioportalAPIKey) {
		this.authenticationToken = authenticationToken;
		this.config = config;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		
		this.setArgs(createArgs());
		//could be reduced to only libraries relevant to semantic-markup
		this.setClassPath(System.getProperty("java.class.path"));
		this.setMainClass(ETCLearnMain.class);
	}
	
	private String[] createArgs() {
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
		
		fileService.createDirectory(new AdminAuthenticationToken(), workspace, tablePrefix, false);
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		/*fileService.createDirectory(new AdminAuthenticationToken(), workspace + File.separator + tablePrefix, "in", false);
		String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		fileService.copyFiles(new AdminAuthenticationToken(), input,  newInput);
		
		XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
		for(File child : new File(newInput).listFiles()) {
			xmlNamespaceManager.removeXmlSchema(child);
		}*/
		List<String> argList = new LinkedList<String>();
		addArg(argList, "a", workspace);
		addArg(argList, "f", source);
		addArg(argList, "g", operator);
		addArg(argList, "j", bioportalUserId);
		addArg(argList, "k", bioportalAPIKey);
		addArg(argList, "b", debugFile);
		addArg(argList, "e", errorFile);
		addArg(argList, "c", config);
		addArg(argList, "w", wordnet);
		addArg(argList, "l", perl);
		addArg(argList, "n", databaseHost);
		addArg(argList, "p", databasePort);
		addArg(argList, "d", databaseName);
		addArg(argList, "u", databaseUser);
		addArg(argList, "s", databasePassword);
		addArg(argList, "i", input);
		addArg(argList, "z", tablePrefix);
		addArg(argList, "o", otoLiteURL);

		String[] args = argList.toArray(new String[argList.size()]);
		
		//Extra JVM Process will not be able to forward empty parameter values to process in a meaningful way..
		/*String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, 
				"-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL};*/
		return args;
	}
	

	private void addArg(List<String> argList, String arg, String value) {
		if(value != null && !value.isEmpty()) {
			argList.add("-" + arg);
			argList.add(value);
		}
	}

	@Override
	public LearnResult createReturn() {
		DatasetPrefix datasetPrefix = DatasetPrefixDAO.getInstance().getDatasetPrefix(tablePrefix);
		LearnResult result = new LearnResult(datasetPrefix.getOtoUploadId(), datasetPrefix.getOtoSecret());
		return result;
	}

}

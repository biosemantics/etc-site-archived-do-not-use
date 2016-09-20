package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.semanticmarkup.ETCLearnMain;

public class ExtraJvmLearn extends ExtraJvmCallable<LearnResult> implements Learn {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				ETCLearnMain.main(args);
			} catch (Throwable t) {
				System.exit(-1);
			}
		}
		
	}
	
	private String config;
	private String input;
	private String tablePrefix;
	private String source;
	private String operator;
	private boolean useEmptyGlossary;
	private DAOManager daoManager;

	public ExtraJvmLearn(DAOManager daoManager, String config, boolean useEmptyGlossary, String input, String tablePrefix,
			String source, String operator) throws SemanticMarkupException {
		this.config = config;
		this.useEmptyGlossary = useEmptyGlossary;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.daoManager = daoManager;
		
		try {
			this.setArgs(createArgs());
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new SemanticMarkupException();
		}
		if(!Configuration.charaparser_xms.isEmpty()) 
			this.setXms(Configuration.charaparser_xms);
		if(!Configuration.charaparser_xmx.isEmpty()) 
			this.setXmx(Configuration.charaparser_xmx);
		
		//could be reduced to only libraries relevant to semantic-markup
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(MainWrapper.class);
	}
	
	private String[] createArgs() throws PermissionDeniedException, CreateDirectoryFailedException {
		//fileService.createDirectory(new AdminAuthenticationToken(), workspace, tablePrefix, false);
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		/*fileService.createDirectory(new AdminAuthenticationToken(), workspace + File.separator + tablePrefix, "in", false);
		String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		fileService.copyFiles(new AdminAuthenticationToken(), input,  newInput);
		
		XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
		for(File child : new File(newInput).listFiles()) {
			xmlNamespaceManager.removeXmlSchema(child);
		}*/
		List<String> argList = new LinkedList<String>();
		addArg(argList, "f", source);
		addArg(argList, "g", operator);
		addArg(argList, "c", config);
		addArg(argList, "i", input);
		addArg(argList, "z", tablePrefix);
		if(useEmptyGlossary)
			addArg(argList, "x");

		String[] args = argList.toArray(new String[argList.size()]);
		
		//Extra JVM Process will not be able to forward empty parameter values to process in a meaningful way..
		/*String[] args = new String[] { "-a", workspace, "-f", source, "-g", operator, "-j", bioportalUserId, "-k", bioportalAPIKey, "-b", debugFile, 
				"-e", errorFile, "-c", config, "-w", wordnet, "-l", perl,
				"-n", databaseHost, "-p", databasePort, "-d", databaseName, "-u", databaseUser, 
				"-s", databasePassword, "-i", input, "-z" , tablePrefix, "-y", "-o", otoLiteURL};*/
		return args;
	}
	

	private void addArg(List<String> argList, String arg) {
		argList.add("-" + arg);
	}

	private void addArg(List<String> argList, String arg, String value) {
		if(value != null && !value.isEmpty()) {
			argList.add("-" + arg);
			argList.add(value);
		}
	}

	@Override
	public LearnResult createReturn() throws SemanticMarkupException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Semantic Markup Learn failed.");
			throw new SemanticMarkupException();
		}
		DatasetPrefix datasetPrefix = daoManager.getSemanticMarkupDBDAO().getDatasetPrefix(tablePrefix);
		if(datasetPrefix == null)
			throw new SemanticMarkupException();
		LearnResult result = new LearnResult(datasetPrefix.getOtoUploadId(), datasetPrefix.getOtoSecret());
		return result;
	}

}

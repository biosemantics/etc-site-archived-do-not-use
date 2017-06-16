package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
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

public class InJvmLearn implements Learn {
	
	private String config;
	private String input;
	private String tablePrefix;
	private String source;
	private String operator;
	private boolean executedSuccessfully = false;
	private boolean useEmptyGlossary;
	private IFileService fileService;
	private DAOManager daoManager;

	public InJvmLearn(DAOManager daoManager, IFileService fileService, String config, boolean useEmptyGlossary, String input, String tablePrefix,
			String source, String operator) {
		this.fileService = fileService;
		this.config = config;
		this.useEmptyGlossary = useEmptyGlossary;
		this.input = input;
		this.tablePrefix = tablePrefix;
		this.source = source;
		this.operator = operator;
		this.daoManager = daoManager;
	}
	
	@Override
	public LearnResult call() throws SemanticMarkupException {
		String workspace = Configuration.charaparser_tempFileBase;
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), workspace, tablePrefix, false);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e1) {
			throw new SemanticMarkupException();
		}
		
		//only temporary until charaparser can deal with the namespaces and they don't need to be pre- and post treated with XmlNamespaceManager
		/*fileService.createDirectory(new AdminAuthenticationToken(), workspace + File.separator + tablePrefix, "in", false);
		String newInput = workspace + File.separator + tablePrefix + File.separator + "in";
		fileService.copyFiles(new AdminAuthenticationToken(), input,  newInput);
		
		XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
		for(File child : new File(newInput).listFiles()) {
			xmlNamespaceManager.removeXmlSchema(child);
		}*/
		String[] args = new String[] {"-f", source, "-g", operator, "-c", config, "-i", input, "-z" , tablePrefix};
		if(useEmptyGlossary) {
			List<String> argList = new ArrayList<String>(Arrays.asList(args));
			argList.add("-x");
			args = argList.toArray(new String[argList.size()]);
		}
		
		try {
			//for(String s: args) System.out.print(s+" ");
			//System.out.println(" ");
			ETCLearnMain.main(args);
			DatasetPrefix datasetPrefix = daoManager.getSemanticMarkupDBDAO().getDatasetPrefix(tablePrefix);
			LearnResult result = new LearnResult(datasetPrefix.getOtoUploadId(), datasetPrefix.getOtoSecret());	
			executedSuccessfully = true;
			return result;
			//throw new SemanticMarkupException(); //test failing process
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Semantic Markup Learn failed with exception.", e);
			executedSuccessfully = false;
			throw new SemanticMarkupException();
		}
	}
	
	@Override
	public void destroy() {}

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}


package edu.arizona.biosemantics.etcsite.server.rpc.treegeneration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.TreeGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.treegeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.TreeGenerationException;
import edu.ucdavis.cs.cfgproject.server.CSVReader;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class TreeGenerationService extends RemoteServiceServlet implements ITreeGenerationService {
	
	private IFileService fileService;
	private IFileFormatService fileFormatService;
	private IFileAccessService fileAccessService;
	private IFilePermissionService filePermissionService;
	private DAOManager daoManager;
	private CSVReader reader = new CSVReader(CSVParser.DEFAULT_SEPARATOR, '|', CSVParser.DEFAULT_QUOTE_CHARACTER, 
			CSVParser.DEFAULT_ESCAPE_CHARACTER);
	
	@Inject
	public TreeGenerationService(IFileService fileService, IFileFormatService fileFormatService, 
			IFileAccessService fileAccessService, IFilePermissionService filePermissionservice, DAOManager daoManager) {
		this.fileService = fileService;
		this.fileFormatService = fileFormatService;
		this.fileAccessService = fileAccessService;
		this.filePermissionService = filePermissionservice;
		this.daoManager = daoManager;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TREE_GENERATION)) {
						return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws TreeGenerationException {
		final TreeGenerationConfiguration config = getTreeGenerationConfiguration(task);
		
		//remove tree generation configuration
		if(config.getInput() != null)
			try {
				fileService.setInUse(authenticationToken, false, config.getInput(), task);
			} catch (PermissionDeniedException e) {
				throw new TreeGenerationException(task);
			}
		daoManager.getTreeGenerationConfigurationDAO().remove(config);
		
		//remove task
		daoManager.getTaskDAO().removeTask(task);
		if(task.getConfiguration() != null)
			//remove configuration
			daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
	
		//cancel possible futures: not for this task type
	}
	
	@Override
	public Boolean isValidInput(AuthenticationToken token, String inputFile) {
		File file = new File(inputFile);
		if(!file.isDirectory())
			return false;
		int count = file.listFiles().length;
		if(count == 1 || count == 2) { 
			int ser = 0;
			int csv = 0;
			for(File child : file.listFiles()) {
				if(child.getName().endsWith(".ser")) {
					ser++;
				}
				if(child.getName().endsWith(".csv")) {
					csv++;
				}
			}
			return (csv == 1 && ser == 1) || csv == 1;
		}
		return false;
	}

	@Override
	public Task start(AuthenticationToken token, String taskName, String input) throws TreeGenerationException {
		boolean isShared = filePermissionService.isSharedFilePath(token.getUserId(), input);
		String fileName;
		try {
			fileName = fileService.getFileName(token, input);
		} catch (PermissionDeniedException e) {
			throw new TreeGenerationException();
		}
		if(isShared) {
			String destination;
			try {
				destination = fileService.createDirectory(token, Configuration.fileBase + File.separator + token.getUserId(), 
						fileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new TreeGenerationException();
			}
			try {
				fileService.copyFiles(token, input, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new TreeGenerationException();
			}
			input = destination;
		}
		
		TreeGenerationConfiguration config = new TreeGenerationConfiguration();
		config.setInput(input);	
		config = daoManager.getTreeGenerationConfigurationDAO().addTreeGenerationConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TREE_GENERATION;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTreeGenerationTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(token.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getTreeGenerationTaskStage(TaskStageEnum.VIEW.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);

		try {
			fileService.setInUse(token, true, input, task);
		} catch (PermissionDeniedException e) {
			throw new TreeGenerationException(task);
		}
		return task;
	}
	
	@Override
	public TaxonMatrix view(AuthenticationToken authenticationToken, Task task) throws TreeGenerationException {
		final TreeGenerationConfiguration config = getTreeGenerationConfiguration(task);
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TREE_GENERATION);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTreeGenerationTaskStage(TaskStageEnum.VIEW.toString());
		task.setTaskStage(taskStage);
		task.setResumable(false);
		task.setComplete(true);
		task.setCompleted(new Date());
		daoManager.getTaskDAO().updateTask(task);
		task = daoManager.getTaskDAO().getTask(task.getId());
		
		File file = new File(config.getInput());
		File matrixFile = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".csv");
			} 
		})[0];
		try {
			return reader.read(matrixFile.getAbsolutePath());
		} catch(IOException e) {
			String message = "Couldn't read input";
			log(LogLevel.ERROR, message, e);
			throw new TreeGenerationException(message, task);
		}
	}

	private TreeGenerationConfiguration getTreeGenerationConfiguration(Task task) throws TreeGenerationException {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof TreeGenerationConfiguration))
			throw new TreeGenerationException(task);
		final TreeGenerationConfiguration treeGenerationConfiguration = (TreeGenerationConfiguration)configuration;
		return treeGenerationConfiguration;
	}

	@Override
	public Task goToTaskStage(AuthenticationToken token, Task task,	TaskStageEnum taskStageEnum) {
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TREE_GENERATION);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTreeGenerationTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}
	
	@Override
	public List<Task> getResumables(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getResumableTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TREE_GENERATION)) {
				result.add(task);
			}
		}
		return result;
	}
}

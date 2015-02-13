package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
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
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {
	
	private IFileService fileService = new FileService();
	private IFileFormatService fileFormatService = new FileFormatService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Void>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Void>>();
	private Map<Integer, TaxonomyComparison> activeProcess = new HashMap<Integer, TaxonomyComparison>();
	private DAOManager daoManager = new DAOManager();
	private Emailer emailer = new Emailer();
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Task start(AuthenticationToken authenticationToken, String taskName, String input) throws TaxonomyComparisonException {	
		boolean isShared = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), input);
		String fileName = null;
		try {
			fileName = fileService.getFileName(authenticationToken, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Permission denied to read "+fileName);
			throw new TaxonomyComparisonException();
		}
		if(isShared) {
			String destination;
			try {
				destination = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
						fileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new TaxonomyComparisonException();
			}
			try {
				fileService.copyFiles(authenticationToken, input, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new TaxonomyComparisonException();
			}
			input = destination;
		}
		
		TaxonomyComparisonConfiguration config = new TaxonomyComparisonConfiguration();
		config.setInput(input);	
		config.setOutput(config.getInput() + "_output_by_TaxC_task_" + taskName);
		config = daoManager.getTaxonomyComparisonConfigurationDAO().addTaxonomyComparisonConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TAXONOMY_COMPARISON;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.INPUT.toString());
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.PROCESS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);

		try {
			fileService.setInUse(authenticationToken, true, input, task);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "can not set file in use for "+input+ ".");
			throw new TaxonomyComparisonException(task);
		}
		return task;
	}

	
	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.TAXONOMY_COMPARISON)) {
						return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws TaxonomyComparisonException {
		final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
						
		//remove taxonomy comparison configuration
		if(config.getInput() != null)
			try {
				fileService.setInUse(authenticationToken, false, config.getInput(), task);
			} catch (PermissionDeniedException e) {
				throw new TaxonomyComparisonException(task);
			}
		daoManager.getTaxonomyComparisonConfigurationDAO().remove(config);
		
		//remove task
		daoManager.getTaskDAO().removeTask(task);
		if(task.getConfiguration() != null)
			//remove configuration
			daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
	
		//cancel possible futures
		if(task.getTaskStage() != null) {
			switch(TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
			case INPUT:
				break;
			case PROCESS:
				if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
					ListenableFuture<Void> processFuture = this.activeProcessFutures.get(config.getConfiguration().getId());
					processFuture.cancel(true);
				}
				if(activeProcess.containsKey(config.getConfiguration().getId())) {
					activeProcess.get(config.getConfiguration().getId()).destroy();
				}
				break;
			case VIEW:
				break;
			default:
				break;
			}
		}
	}

	private TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration(Task task) throws TaxonomyComparisonException {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof TaxonomyComparisonConfiguration))
			throw new TaxonomyComparisonException(task);
		final TaxonomyComparisonConfiguration taxonomyComparisonConfiguration = (TaxonomyComparisonConfiguration)configuration;
		return taxonomyComparisonConfiguration;
	}

	@Override
	public boolean isValidInput(AuthenticationToken token, String inputFile) {
		return true;
	}

	@Override
	public Task process(AuthenticationToken token, Task task) {
		return task;
	}
	
}

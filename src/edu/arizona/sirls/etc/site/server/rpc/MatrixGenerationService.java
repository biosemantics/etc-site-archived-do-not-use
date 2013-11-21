package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.ConfigurationDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfigurationDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskStage;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskStageDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskType;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskTypeDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private int maximumThreads = 10;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Boolean>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Boolean>>();

	
	public MatrixGenerationService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
	}
	
	@Override
	public RPCResult<MatrixGenerationTaskRun> start(AuthenticationToken authenticationToken, String taskName, String input) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		
		if(!authResult.isSucceeded()) 
			return new RPCResult<MatrixGenerationTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<MatrixGenerationTaskRun>(false, "Authentication failed");
		
		try {
			MatrixGenerationConfiguration matrixGenerationConfiguration = new MatrixGenerationConfiguration();
			matrixGenerationConfiguration.setInput(input);	
			matrixGenerationConfiguration = MatrixGenerationConfigurationDAO.getInstance().addMatrixGenerationConfiguration(matrixGenerationConfiguration);
			
			edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskType = edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION;
			TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.INPUT.toString());
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task.setTaskConfiguration(matrixGenerationConfiguration);
			task.setTaskType(dbTaskType);
			
			task = TaskDAO.getInstance().addTask(task);
			taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
			task.setTaskStage(taskStage);
			TaskDAO.getInstance().updateTask(task);

			fileService.setInUse(authenticationToken, true, input, task);
			return new RPCResult<MatrixGenerationTaskRun>(true, new MatrixGenerationTaskRun(matrixGenerationConfiguration, task));
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<MatrixGenerationTaskRun>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<MatrixGenerationTaskRun> process(AuthenticationToken authenticationToken, final MatrixGenerationTaskRun matrixGenerationTaskRun) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<MatrixGenerationTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<MatrixGenerationTaskRun>(false, "Authentication failed");
		
		try {
			final MatrixGenerationConfiguration matrixGenerationConfiguration = matrixGenerationTaskRun.getConfiguration();
			//browser back button may invoke another "learn"
			if(activeProcessFutures.containsKey(matrixGenerationConfiguration.getConfiguration().getId())) {
				return new RPCResult<MatrixGenerationTaskRun>(true, matrixGenerationTaskRun);
			} else {
				final Task task = TaskDAO.getInstance().getTask(matrixGenerationConfiguration.getConfiguration());
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String input = matrixGenerationConfiguration.getInput();
				RPCResult<Void> createDirResult = fileService.createDirectory(new AdminAuthenticationToken(), Configuration.tempFileBase, String.valueOf(task.getId()));
				if(!createDirResult.isSucceeded()) 
					return new RPCResult<MatrixGenerationTaskRun>(false, createDirResult.getMessage());
				String outputFile = Configuration.tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
				
				MatrixGeneration matrixGeneration = new MatrixGeneration(input, outputFile);
				final ListenableFuture<Boolean> futureResult = executorService.submit(matrixGeneration);
				this.activeProcessFutures.put(matrixGenerationConfiguration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			activeProcessFutures.remove(matrixGenerationConfiguration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				Boolean result = futureResult.get();
									TaskStage newTaskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.OUTPUT.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									TaskDAO.getInstance().updateTask(task);
				     			}
							} catch (Exception e) {
								e.printStackTrace();
							}
				     	}
				     }, executorService);
				
				return new RPCResult<MatrixGenerationTaskRun>(true, matrixGenerationTaskRun);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<MatrixGenerationTaskRun>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Void> output(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTaskRun) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		try {
			Task task = matrixGenerationTaskRun.getTask();
			MatrixGenerationConfiguration matrixGenerationConfiguration = matrixGenerationTaskRun.getConfiguration();
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + task.getName());
			MatrixGenerationConfigurationDAO.getInstance().updateMatrixGenerationConfiguration(matrixGenerationConfiguration);

			String outputDirectory = matrixGenerationConfiguration.getOutput();			
			RPCResult<String> outputDirectoryParentResult = fileService.getParent(authenticationToken, outputDirectory);
			RPCResult<String> outputDirectoryNameResult = fileService.getFileName(authenticationToken, outputDirectory);
			if(!outputDirectoryParentResult.isSucceeded() || !outputDirectoryNameResult.isSucceeded())
				return new RPCResult<Void>(false, outputDirectoryParentResult.getMessage());
			
			//find a suitable destination filePath
			RPCResult<String> createDirectoryResult = fileService.createDirectoryForcibly(authenticationToken, outputDirectoryParentResult.getData(), outputDirectoryNameResult.getData());
			if(!createDirectoryResult.isSucceeded()) 
				return new RPCResult<Void>(false, createDirectoryResult.getMessage());
			
			//copy the output files to the directory
			String matrixGenerationOutputDirectory = Configuration.tempFileBase + File.separator + task.getId();		
			RPCResult<Void> copyResult = fileService.copyFiles(new AdminAuthenticationToken(), matrixGenerationOutputDirectory, createDirectoryResult.getData());
			if(!copyResult.isSucceeded())
				return copyResult;
			
			//update task
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			TaskDAO.getInstance().updateTask(task);
			
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<MatrixGenerationTaskRun> getLatestResumable(AuthenticationToken authenticationToken) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<MatrixGenerationTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<MatrixGenerationTaskRun>(false, "Authentication failed");
		
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Task> tasks = TaskDAO.getInstance().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable()) {
					MatrixGenerationConfiguration configuration = MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfiguration(task.getTaskConfiguration().getConfiguration().getId());
					return new RPCResult<MatrixGenerationTaskRun>(true,
							new MatrixGenerationTaskRun(configuration, task));
				}
			}
			return new RPCResult<MatrixGenerationTaskRun>(false, "No resumable task found");
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<MatrixGenerationTaskRun>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<MatrixGenerationTaskRun> getMatrixGenerationTaskRun(AuthenticationToken authenticationToken, Task task) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<MatrixGenerationTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<MatrixGenerationTaskRun>(false, "Authentication failed");
		
		try {
			task = TaskDAO.getInstance().getTask(task.getId());
			MatrixGenerationConfiguration configuration = 
					MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfiguration(task.getTaskConfiguration().getConfiguration().getId());
			return new RPCResult<MatrixGenerationTaskRun>(true, 
					new MatrixGenerationTaskRun(configuration, task));
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<MatrixGenerationTaskRun>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		try {
			RPCResult<MatrixGenerationTaskRun> matrixGenerationTaskRunResult = 
					getMatrixGenerationTaskRun(authenticationToken, task);
			if(!matrixGenerationTaskRunResult.isSucceeded()) 
				return new RPCResult<Void>(false, matrixGenerationTaskRunResult.getMessage());
			MatrixGenerationTaskRun matrixGenerationTaskRun = matrixGenerationTaskRunResult.getData();
			MatrixGenerationConfiguration matrixGenerationConfiguration = matrixGenerationTaskRun.getConfiguration();
						
			//remove matrix generation configuration
			if(matrixGenerationConfiguration != null) {
				if(matrixGenerationConfiguration.getInput() != null)
					fileService.setInUse(authenticationToken, false, matrixGenerationConfiguration.getInput(), task);
				MatrixGenerationConfigurationDAO.getInstance().remove(matrixGenerationConfiguration);
			}
			
			//remove task
			if(task != null) {
				TaskDAO.getInstance().removeTask(task);
				if(task.getTaskConfiguration() != null)
					
					//remove configuration
					ConfigurationDAO.getInstance().remove(task.getTaskConfiguration().getConfiguration());
			
				//cancel possible futures
				if(task.getTaskStage() != null) {
					switch(TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
					case INPUT:
						break;
					case PROCESS:
						if(activeProcessFutures.containsKey(matrixGenerationConfiguration.getConfiguration().getId())) {
							ListenableFuture<Boolean> processFuture = this.activeProcessFutures.get(matrixGenerationConfiguration.getConfiguration().getId());
							processFuture.cancel(true);
						}
						break;
					case OUTPUT:
						break;
					default:
						break;
					}
				}
			}
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		super.destroy();
	}


}

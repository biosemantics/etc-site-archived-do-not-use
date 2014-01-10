package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.db.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.ConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.MatrixGenerationConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStageDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskType;
import edu.arizona.biosemantics.etcsite.shared.db.TaskTypeDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TasksOutputFilesDAO;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Taxon;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IFileService fileService = new FileService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private int maximumThreads = 10;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Boolean>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Boolean>>();

	
	public MatrixGenerationService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
	}
	
	@Override
	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String input) {	
		try {
			RPCResult<Boolean> sharedResult = filePermissionService.isSharedFilePath(authenticationToken.getUsername(), input);
			if(!sharedResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't verify permission on input directory");
			RPCResult<String> fileNameResult = fileService.getFileName(authenticationToken, input);
			if(!fileNameResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't find file name for import");
			if(sharedResult.getData()) {
				RPCResult<String> destinationResult = 
						fileService.createDirectoryForcibly(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUsername(), fileNameResult.getData());
				RPCResult<Void> destination = fileService.copyFiles(authenticationToken, input, destinationResult.getData());
				if(!destinationResult.isSucceeded() || !destination.isSucceeded())
					return new RPCResult<Task>(false, "Couldn't copy shared files to an owned destination for input to task");
				input = destinationResult.getData();
			}
			
			MatrixGenerationConfiguration matrixGenerationConfiguration = new MatrixGenerationConfiguration();
			matrixGenerationConfiguration.setInput(input);	
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + taskName);
			matrixGenerationConfiguration = MatrixGenerationConfigurationDAO.getInstance().addMatrixGenerationConfiguration(matrixGenerationConfiguration);
			
			edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION;
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
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> process(AuthenticationToken authenticationToken, final Task task) {	
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
				
			//browser back button may invoke another "learn"
			if(activeProcessFutures.containsKey(configuration.getConfiguration().getId())) {
				return new RPCResult<Task>(true, task);
			} else {
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String input = matrixGenerationConfiguration.getInput();
				RPCResult<Void> createDirResult = fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()));
				if(!createDirResult.isSucceeded()) 
					return new RPCResult<Task>(false, createDirResult.getMessage());
				fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()));
				String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
				
				MatrixGeneration matrixGeneration = new MatrixGeneration(input, outputFile);
				final ListenableFuture<Boolean> futureResult = executorService.submit(matrixGeneration);
				this.activeProcessFutures.put(configuration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			activeProcessFutures.remove(configuration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				Boolean result = futureResult.get();
									TaskStage newTaskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.REVIEW.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									TaskDAO.getInstance().updateTask(task);
				     			}
							} catch (Exception e) {
								e.printStackTrace();
							}
				     	}
				     }, executorService);
				
				return new RPCResult<Task>(true, task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Matrix> review(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Matrix>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
			Matrix matrix = readFile(outputFile);
			return new RPCResult<Matrix>(true, matrix);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Matrix>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> completeReview(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.OUTPUT.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			TaskDAO.getInstance().updateTask(task);
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
		

	}

	private Matrix readFile(String filePath) throws Exception {
		Matrix result = new Matrix();
		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		boolean firstLine = true;
		int id=0;
		while ((line = br.readLine()) != null) {
			String[] values = line.split("\t");
			if(firstLine) {
				for(int i=1; i<values.length; i++) {
					String characterName = values[i];
					result.addCharacterName(characterName);
				}
			} else {
				Taxon taxon = new Taxon();
				taxon.setName(values[0]);
				taxon.setId(String.valueOf(id));
				for(int i=1; i<values.length; i++) {
					taxon.setCharacterState(result.getCharacterNames().get(i-1), values[i]);
				}
				result.addTaxon(taxon);
				id++;
			}
			firstLine = false;
		}
		br.close();
		return result;
	}

	@Override
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task task) {
		try {
			AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + task.getName());
			
			String outputDirectory = matrixGenerationConfiguration.getOutput();			
			RPCResult<String> outputDirectoryParentResult = fileService.getParent(authenticationToken, outputDirectory);
			RPCResult<String> outputDirectoryNameResult = fileService.getFileName(authenticationToken, outputDirectory);
			if(!outputDirectoryParentResult.isSucceeded() || !outputDirectoryNameResult.isSucceeded())
				return new RPCResult<Task>(false, outputDirectoryParentResult.getMessage());
			
			//find a suitable destination filePath
			RPCResult<String> createDirectoryResult = fileService.createDirectoryForcibly(authenticationToken, outputDirectoryParentResult.getData(), outputDirectoryNameResult.getData());
			if(!createDirectoryResult.isSucceeded()) 
				return new RPCResult<Task>(false, createDirectoryResult.getMessage());
			
			//copy the output files to the directory
			String matrixGenerationOutputDirectory = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId();		
			RPCResult<Void> copyResult = fileService.copyFiles(new AdminAuthenticationToken(), matrixGenerationOutputDirectory, createDirectoryResult.getData());
			if(!copyResult.isSucceeded()) {
				return new RPCResult<Task>(false, copyResult.getMessage());
			}
			
			//update task
			matrixGenerationConfiguration.setOutput(createDirectoryResult.getData());
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			TaskDAO.getInstance().updateTask(task);
			
			TasksOutputFilesDAO.getInstance().addOutput(task, createDirectoryResult.getData());
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken) {
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Task> tasks = TaskDAO.getInstance().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable() && 
						task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION)) {
							//SemanticMarkupConfiguration configuration = SemanticMarkupConfigurationDAO.getInstance().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
							return new RPCResult<Task>(true, task);
				}
			}
			return new RPCResult<Task>(false, "No resumable task found");
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task) {
		try {
			AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
						
			//remove matrix generation configuration
			if(matrixGenerationConfiguration != null) {
				if(matrixGenerationConfiguration.getInput() != null)
					fileService.setInUse(authenticationToken, false, matrixGenerationConfiguration.getInput(), task);
				MatrixGenerationConfigurationDAO.getInstance().remove(matrixGenerationConfiguration);
			}
			
			//remove task
			if(task != null) {
				TaskDAO.getInstance().removeTask(task);
				if(task.getConfiguration() != null)
					
					//remove configuration
					ConfigurationDAO.getInstance().remove(task.getConfiguration().getConfiguration());
			
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

	@Override
	public RPCResult<Void> save(AuthenticationToken authenticationToken, Matrix matrix, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
			saveFile(matrix, outputFile);
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	private void saveFile(Matrix matrix, String outputFile) throws IOException {
		File file = new File(outputFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		bw.write("Name\t");
		for(String characterName : matrix.getCharacterNames()) {
			bw.write(characterName + "\t");
		}
		bw.write("\n");
		
		for(Taxon taxon : matrix.getTaxons()) {
			bw.write(taxon.getName() + "\t");
			for(String characterName : matrix.getCharacterNames()) {
				bw.write(taxon.getCharacterState(characterName) + "\t");
			}
			bw.write("\n");
		}
		bw.close();
	}

	@Override
	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {
		try {
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(taskStageEnum.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			task.setComplete(false);
			task.setCompleted(null);
			TaskDAO.getInstance().updateTask(task);
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

}

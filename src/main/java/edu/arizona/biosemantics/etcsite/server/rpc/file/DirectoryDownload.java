package edu.arizona.biosemantics.etcsite.server.rpc.file;

import java.io.File;
import java.util.List;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Zipper;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.FileDeleteFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskService;

public class DirectoryDownload {

	private String filePath;
	private AuthenticationToken authenticationToken;
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private ITaskService taskService = new TaskService();
	private IFileService fileService = new FileService();
	private String error = "";
	private String zipFilepath = "";
	private DAOManager daoManager = new DAOManager();
	
	public DirectoryDownload(AuthenticationToken authenticationToken, String filePath) {
		super();
		this.filePath = filePath;
		this.authenticationToken = authenticationToken;
	}

	public boolean execute() throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		Zipper zipper = new Zipper();
		String zipSource = null;
		
		if((filePath.startsWith("Share.") || filePath.equals("Root") || filePath.equals("Owned") || filePath.equals("Shared"))) {
			if(filePath.equals("Root")) { 
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + "Root";
				gatherRoot(zipSource);
			}
			if(filePath.equals("Shared")) {
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + "Shared";
				gatherShared(zipSource);
			}
			if(filePath.equals("Owned")) {
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + "Owned";
				gatherOwned(zipSource);
			}
			if(filePath.startsWith("Share.Input")) {
				Task task = this.getTaskFromFilePath(filePath);
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + new FileNameNormalizer().normalize(task.getName()) + "Input";
				gatherShareInput(this.getTaskFromFilePath(filePath), zipSource);
			}
			if(filePath.startsWith("Share.Output")) {
				Task task = this.getTaskFromFilePath(filePath);
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + new FileNameNormalizer().normalize(task.getName()) + "Output";
				gatherShareOutput(task, zipSource);
			}
			if(filePath.matches("Share\\.\\d*")) {
				Task task = this.getTaskFromFilePath(filePath);
				zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + new FileNameNormalizer().normalize(task.getName());
				gatherShare(task, zipSource);
			}
		} else {
			boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);		
			if(permissionResult) {			
				File file = new File(filePath);
				
				String fileName = file.getName();
				if(file.equals(new File(Configuration.fileBase + File.separator + authenticationToken.getUserId())))
					fileName = "Owned";
					
				if(file.exists()) {
					zipSource = gatherFiles(file, 
							Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + fileName);					
				} else {
					this.error = "File was not found";
					return false;
				}
			} else {
				this.error = "Permission denied";
				return false;
			}
		}
		
		if(zipSource != null) {
			zipFilepath = zipSource + ".zip";
			zipFilepath = zipper.zip(zipSource, zipFilepath);
			if(zipFilepath != null)
				return true;
			else {
				this.error = "Zipping failed";
				return false;
			}
		} 
		this.error = "Permission denied";
		return false;
	}
	
	public String getError() {
		return this.error;
	}

	public String getZipPath() {
		return this.zipFilepath;
	}
	
	private String gatherFiles(File file, String destination) throws 
		PermissionDeniedException, FileDeleteFailedException, CopyFilesFailedException, CreateDirectoryFailedException {
		File destinationFile = new File(destination);
		fileService.deleteFile(authenticationToken, destination);
		fileService.createDirectory(authenticationToken, destinationFile.getParent(), destinationFile.getName(), false);				
		fileService.copyFiles(authenticationToken, file.getAbsolutePath(), destination);
		return destination;
	}
	
	private void gatherRoot(String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		String ownedDir = destination + File.separator + "Owned";
		this.gatherOwned(ownedDir);
		
		String sharedDir = destination + File.separator + "Shared";
		this.gatherShared(sharedDir);	
	}
	
	private void gatherOwned(String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		cleanup(destination);		
		String source = Configuration.fileBase + File.separator + authenticationToken.getUserId();
		fileService.copyFiles(authenticationToken, source, destination);
	}
	
	private void gatherShared(String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		cleanup(destination);
		ITaskService taskService = new TaskService();
		List<Task> sharedTasks = taskService.getSharedWithTasks(authenticationToken);
		for(Task task : sharedTasks) {
			String taskDestination = destination + File.separator + new FileNameNormalizer().normalize(task.getName());
			this.gatherShare(task, taskDestination);
		}
	}
	
	private void gatherShare(Task task, String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		cleanup(destination);
		String inputDir = destination + File.separator + "Input";
		this.gatherShareInput( task, inputDir);
		
		String outputDir = destination + File.separator + "Output";
		this.gatherShareOutput(task, outputDir);		
	}
	
	private void gatherShareInput(Task task, String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		cleanup(destination);
		List<String> inputs = task.getConfiguration().getInputs();
		for(String input : inputs) {
			File inputFile = new File(input);
			if(inputFile.exists()) {
				fileService.copyFiles(authenticationToken, input, destination);
			}
		}
	}
	
	private void gatherShareOutput(Task task, String destination) throws CopyFilesFailedException, PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		cleanup(destination);
		List<String> outputs = daoManager.getTasksOutputFilesDAO().getOutputs(task);
		for(String output : outputs) {
			File outputFile = new File(output);
			if(outputFile.exists()) {
				fileService.copyFiles(authenticationToken, output, destination);
			}
		}
	}

	private void cleanup(String destination) throws PermissionDeniedException, FileDeleteFailedException, CreateDirectoryFailedException {
		File destinationFile = new File(destination);
		File parent = destinationFile.getParentFile();
		String fileName = destinationFile.getName();
		String normalizedFileName = new FileNameNormalizer().normalize(fileName);
		
		destinationFile = new File(parent, normalizedFileName);
		
		fileService.deleteFile(authenticationToken, destinationFile.getAbsolutePath());
		fileService.createDirectory(authenticationToken, destinationFile.getParent(), destinationFile.getName(), false);
	}


	//TODO: add some security for the task really being shared with the user
	//add some general task permission check service
	private Task getTaskFromFilePath(String filePath) {
		System.out.println("1" + filePath);
		System.out.println("2" + filePath.lastIndexOf("."));
		System.out.println("3" + filePath.substring(filePath.lastIndexOf(".") + 1));
		int taskId = Integer.parseInt(filePath.substring(filePath.lastIndexOf(".") + 1));
		daoManager.getTaskDAO().getTask(taskId);
		Task task = new Task();
		task.setId(taskId);
		return taskService.getTask(authenticationToken, task);
	}
}

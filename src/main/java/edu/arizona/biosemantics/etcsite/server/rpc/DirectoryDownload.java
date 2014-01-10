package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TasksOutputFilesDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class DirectoryDownload {

	private String filePath;
	private AuthenticationToken authenticationToken;
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private ITaskService taskService = new TaskService();
	private IFileService fileService = new FileService();
	private String error = "";
	private String zipFilepath = "";
	
	public DirectoryDownload(AuthenticationToken authenticationToken, String filePath) {
		super();
		this.filePath = filePath;
		this.authenticationToken = authenticationToken;
	}

	public boolean execute() {
		Zipper zipper = new Zipper();
		String zipSource = null;
		
		try {
			if((filePath.startsWith("Share.") || filePath.equals("Root") || filePath.equals("Owned") || filePath.equals("Shared"))) {
				if(filePath.equals("Root")) { 
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + "Root";
					gatherRoot(zipSource);
				}
				if(filePath.equals("Shared")) {
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + "Shared";
					gatherShared(zipSource);
				}
				if(filePath.equals("Owned")) {
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + "Owned";
					gatherOwned(zipSource);
				}
				if(filePath.startsWith("Share.Input")) {
					Task task = this.getTaskFromFilePath(filePath);
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + task.getName() + "Input";
					gatherShareInput(this.getTaskFromFilePath(filePath), zipSource);
				}
				if(filePath.startsWith("Share.Output")) {
					Task task = this.getTaskFromFilePath(filePath);
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + task.getName() + "Output";
					gatherShareOutput(task, zipSource);
				}
				if(filePath.matches("Share\\.\\d*")) {
					Task task = this.getTaskFromFilePath(filePath);
					zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + task.getName();
					gatherShare(task, zipSource);
				}
			} else {
				RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);		
				if(!permissionResult.isSucceeded()) {
					this.error = permissionResult.getMessage();
					return false;
				}
				if(permissionResult.getData()) {			
					File file = new File(filePath);
					if(file.exists()) {
						zipSource = gatherFiles(file, 
								Configuration.compressedFileBase + File.separator + authenticationToken.getUsername() + File.separator + file.getName());					
					} else {
						this.error = "File was not found";
						return false;
					}
				} else {
					this.error = "Permission denied";
					return false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			this.error = "Internal Server Error";
			return false;
		}
		
		if(zipSource != null) {
			try {
				zipFilepath = zipSource + ".tar.gz";
				zipper.zip(zipSource, zipFilepath);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
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
	
	private String gatherFiles(File file, String destination) {
		File destinationFile = new File(destination);
		fileService.deleteFile(authenticationToken, destination);
		fileService.createDirectory(authenticationToken, destinationFile.getParent(), destinationFile.getName());				
		fileService.copyFiles(new AdminAuthenticationToken(), file.getAbsolutePath(), destination);
		return destination;
	}
	
	private void gatherRoot(String destination) throws ClassNotFoundException, SQLException, IOException {
		String ownedDir = destination + File.separator + "Owned";
		this.gatherOwned(ownedDir);
		
		String sharedDir = destination + File.separator + "Shared";
		this.gatherShared(sharedDir);	
	}
	
	private void gatherOwned(String destination) {
		cleanup(destination);		
		String source = Configuration.fileBase + File.separator + authenticationToken.getUsername();
		fileService.copyFiles(authenticationToken, source, destination);
	}
	
	private void gatherShared(String destination) throws ClassNotFoundException, SQLException, IOException {
		cleanup(destination);
		ITaskService taskService = new TaskService();
		RPCResult<List<Task>> sharedTasks = taskService.getSharedWithTasks(authenticationToken);
		if(sharedTasks.isSucceeded()) {
			for(Task task : sharedTasks.getData()) {
				String taskDestination = destination + File.separator + task.getName();
				this.gatherShare(task, taskDestination);
			}
		}
	}
	
	private void gatherShare(Task task, String destination) throws ClassNotFoundException, SQLException, IOException {
		cleanup(destination);
		String inputDir = destination + File.separator + "Input";
		this.gatherShareInput( task, inputDir);
		
		String outputDir = destination + File.separator + "Output";
		this.gatherShareOutput(task, outputDir);		
	}
	
	private void gatherShareInput(Task task, String destination) {
		cleanup(destination);
		List<String> inputs = task.getConfiguration().getInputs();
		for(String input : inputs) {
			File inputFile = new File(input);
			if(inputFile.exists()) {
				fileService.copyFiles(authenticationToken, input, destination);
			}
		}
	}
	
	private void gatherShareOutput(Task task, String destination) throws ClassNotFoundException, SQLException, IOException {
		cleanup(destination);
		List<String> outputs = TasksOutputFilesDAO.getInstance().getOutputs(task);
		for(String output : outputs) {
			File outputFile = new File(output);
			if(outputFile.exists()) {
				fileService.copyFiles(authenticationToken, output, destination);
			}
		}
	}

	private void cleanup(String destination) {
		File destinationFile = new File(destination);
		fileService.deleteFile(authenticationToken, destination);
		fileService.createDirectory(authenticationToken, destinationFile.getParent(), destinationFile.getName());				
	}


	//add some security for the task really being shared with the user
	//add some general task permission check service
	private Task getTaskFromFilePath(String filePath) throws ClassNotFoundException, SQLException, IOException {
		int taskId = Integer.parseInt(filePath.substring(filePath.lastIndexOf(".")));
		TaskDAO.getInstance().getTask(taskId);
		Task task = new Task();
		task.setId(taskId);
		RPCResult<Task> taskResult = taskService.getTask(authenticationToken, task);
		if(taskResult.isSucceeded()) 
			return taskResult.getData();
		return null;
	}
}

package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.Files;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFilePermissionService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.Tree;
import edu.arizona.sirls.etc.site.shared.rpc.db.AbstractTaskConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.FilesInUseDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShareDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskConfigurationDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

public class FileService extends RemoteServiceServlet implements IFileService {

	private static final long serialVersionUID = -9193602268703418530L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private IFileFormatService fileFormatService = new FileFormatService();
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	/**
	 * Cannot hand a fileFilter implementation of .filter(File file) to the method because
	 * file access is not available in the client code. Hence, translation from enum to implementation 
	 * becomes necessary.
	 */
	@Override
	public RPCResult<Tree<FileInfo>> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter) {
		//will become part of 'target' and has effect on several things. therefore leave root as "" for now. Also will have to escape \\'s before it is inserted into SQL statements
		//sql injection..
		//Tree<FileInfo> result = new Tree<FileInfo>(new FileInfo(authenticationToken.getUsername() + "'s files", FileType.DIRECTORY));
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Tree<FileInfo>>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Tree<FileInfo>>(false, "Authentication failed");
		
		Tree<FileInfo> resultTree = new Tree<FileInfo>(new FileInfo("", FileType.DIRECTORY));
		Tree<FileInfo> ownedFiles = new Tree<FileInfo>(new FileInfo("Owned", FileType.DIRECTORY));
		Tree<FileInfo> sharedFiles = new Tree<FileInfo>(new FileInfo("Shared", FileType.DIRECTORY));
		resultTree.addChild(ownedFiles);
		resultTree.addChild(sharedFiles);
		
		try {
			decorateOwnedTree(authenticationToken, ownedFiles, fileFilter, Configuration.fileBase + "//" + authenticationToken.getUsername());
			decorateSharedTree(authenticationToken, sharedFiles, fileFilter);
			return new RPCResult<Tree<FileInfo>>(true, resultTree);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Tree<FileInfo>>(false, e.getMessage());
		}
		
	}
	
	private void decorateSharedTree(AuthenticationToken authenticationToken, Tree<FileInfo> sharedFiles, FileFilter fileFilter) throws Exception {
		ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
		List<Share> shares = ShareDAO.getInstance().getSharesOfInvitee(user);
		
		for(Share share : shares) {
			Tree<FileInfo> shareTree = new Tree<FileInfo>(new FileInfo(share.getTask().getName(), FileType.DIRECTORY));
			sharedFiles.addChild(shareTree);
			AbstractTaskConfiguration taskConfiguration = 
					TaskConfigurationDAO.getInstance().getTaskConfiguration(share.getTask().getConfiguration());
			if(taskConfiguration != null) {
				List<String> inputFiles = taskConfiguration.getInputs();
				List<String> outputFiles = taskConfiguration.getOutputs();
				Tree<FileInfo> inputTree = new Tree<FileInfo>(new FileInfo(share.getTask().getName(), FileType.DIRECTORY));
				shareTree.addChild(inputTree);
				for(String input : inputFiles) {
					File file = new File(input);
					if(file.exists()) {
						RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, input);
						if(!permissionResult.isSucceeded())
							throw new Exception("Couldn't check permission");
						if(permissionResult.getData())
							inputTree.addChild(new Tree<FileInfo>(new FileInfo(file.getName(), getFileType(authenticationToken, input))));
					}
				}
				Tree<FileInfo> outputTree = new Tree<FileInfo>(new FileInfo(share.getTask().getName(), FileType.DIRECTORY));
				shareTree.addChild(outputTree);
				for(String output : outputFiles) {
					File file = new File(output);
					if(file.exists()) {
						RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, output);
						if(!permissionResult.isSucceeded())
							throw new Exception("Couldn't check permission");
						if(permissionResult.getData())
							outputTree.addChild(new Tree<FileInfo>(new FileInfo(file.getName(), getFileType(authenticationToken, output))));
					}
				}
			}
		}
	}

	private void decorateOwnedTree(AuthenticationToken authenticationToken, Tree<FileInfo> fileTree, FileFilter fileFilter, String filePath) throws Exception {
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			throw new Exception("Couldn't check permission");
		if(permissionResult.getData()) {
			File file = new File(filePath);
			for(File child : file.listFiles()) {
				String childPath = child.getAbsolutePath();
				permissionResult = filePermissionService.hasReadPermission(authenticationToken, childPath);
				if(!permissionResult.isSucceeded())
					throw new Exception("Couldn't check permission");
				if(permissionResult.getData()) {
					String name = child.getName();
					FileType fileType = getFileType(authenticationToken, child.getAbsolutePath());
					if(fileType != null && !filter(fileType, fileFilter)) {
						Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(name, fileType));
						fileTree.addChild(childTree);
						if(child.isDirectory()) {
							decorateOwnedTree(authenticationToken, childTree, fileFilter, childPath);
						}
					}
				}
			}
		}
	}

	private boolean filter(FileType fileType, FileFilter fileFilter) {
		switch(fileFilter) {
		case TAXON_DESCRIPTION:
			return !fileType.equals(FileType.TAXON_DESCRIPTION);
		case GLOSSARY:
			return !fileType.equals(FileType.GLOSSARY);
		case EULER:
			return !fileType.equals(FileType.EULER);
		case ALL:
			return false;
		case FILE:
			return fileType.equals(FileType.DIRECTORY);
		case DIRECTORY:
			return !fileType.equals(FileType.DIRECTORY);
		}
		return true;
	}

	private FileType getFileType(AuthenticationToken authenticationToken, String filePath) {
		File file = new File(filePath);
		if(file.isFile()) {
			RPCResult<Boolean> validationResult = fileFormatService.isValidEuler(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.EULER;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.GLOSSARY;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.GLOSSARY;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.TAXON_DESCRIPTION;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.MARKEDUP_TAXON_DESCRIPTION;
		} else if (file.isDirectory())
			return FileType.DIRECTORY;
		return null;
	}

	@Override
	public RPCResult<Void> deleteFile(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, filePath);
			if(inUseResult.isSucceeded() && !inUseResult.getData()) {
				File file = new File(filePath);
				try {
					boolean resultDelete = deleteRecursively(authenticationToken, file);
					return new RPCResult<Void>(resultDelete, resultDelete ? "" : "file delete failed");
				} catch(Exception e) {
					e.printStackTrace();
					return new RPCResult<Void>(false, e.getMessage());
				}
			} else {
				return new RPCResult<Void>(false, createMessageFileInUse(authenticationToken, filePath));
			}
		}
		return new RPCResult<Void>(false, "Permission denied");
	}
	
	private String createMessageFileInUse(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<List<Task>> tasksResult = this.getUsingTasks(authenticationToken, filePath);
		if(!tasksResult.isSucceeded()) 
			return "Couldn't retrieve tasks using the file";
		List<Task> tasks = tasksResult.getData();
		StringBuilder messageBuilder = new StringBuilder("File is in use by tasks: ");
		for(Task task : tasks) 
			messageBuilder.append(task.getName() + ", ");
		String message = messageBuilder.toString();
		return message.substring(0, message.length() - 2);
	}

	private boolean deleteRecursively(AuthenticationToken authenticationToken, File file) throws Exception {
		boolean result = false;
		
		String filePath = file.getAbsolutePath();
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			throw new Exception("Permission denied");
		if(permissionResult.getData()) {
			if (file.isDirectory()) {
				if (file.list().length == 0) {
					result = file.delete();
				} else {
					String files[] = file.list();
					for (String child : files) {
						File fileDelete = new File(file, child);
						result |= deleteRecursively(authenticationToken, fileDelete);
					}
					if (file.list().length == 0) {
						result |= file.delete();
					}
				}
			} else {
				result = file.delete();
			}
		}
		return result;
	}

	@Override
	public RPCResult<Void> moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath) { 
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		File newDirectory = new File(newFilePath).getParentFile();
		if(newDirectory == null) 
			return new RPCResult<Void>(false, "Invalid destination");
		RPCResult<Boolean> permissionResultNewDirectory = filePermissionService.hasWritePermission(authenticationToken, newDirectory.getAbsolutePath());
		if(!permissionResult.isSucceeded() || !permissionResultNewDirectory.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData() && permissionResultNewDirectory.getData()) {
			RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, filePath);
			if(inUseResult.isSucceeded() && !inUseResult.getData()) {
				inUseResult = this.isInUse(authenticationToken, newFilePath);
				if(inUseResult.isSucceeded() && !inUseResult.getData()) {
					File file = new File(filePath);
					File newFile = new File(newFilePath);
					if(file.getAbsolutePath().equals(newFile.getAbsolutePath()))
						return new RPCResult<Void>(true);
					if(newFile.exists())
						return new RPCResult<Void>(false, "File does not exist");
					try {
						Files.move(file, newFile);
						return new RPCResult<Void>(true);
					} catch (IOException e) {
						e.printStackTrace();
						return new RPCResult<Void>(false, "Couldn't move a file");
					}
				} else {
					return new RPCResult<Void>(false, createMessageFileInUse(authenticationToken, newFilePath));
				}
			} else {
				return new RPCResult<Void>(false, createMessageFileInUse(authenticationToken, filePath));
			}
		} 
		return new RPCResult<Void>(false, "Permission denied");		
	}
	
	@Override
	public RPCResult<Void> createDirectory(AuthenticationToken authenticationToken, String filePath, String name) { 
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		if(name.trim().isEmpty()) 
			return new RPCResult<Void>(false, "Directory name may not be empty");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, filePath);
			if(!inUseResult.isSucceeded())
				return new RPCResult<Void>(false, inUseResult.getMessage());
			if(!inUseResult.getData()) {
				File file = new File(filePath + File.separator + name);
				boolean resultMkDir = file.mkdir();
				return new RPCResult<Void>(resultMkDir);
			} else {
				return new RPCResult<Void>(false, createMessageFileInUse(authenticationToken, filePath));
			}
		}
		return new RPCResult<Void>(false, "Permission denied");
	}

	@Override
	public RPCResult<Boolean> isDirectory(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Boolean>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			File file = new File(filePath);
			return new RPCResult<Boolean>(true, file.isDirectory());
		}
		return new RPCResult<Boolean>(false, "Permission denied");
	}

	@Override
	public RPCResult<Boolean> isFile(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Boolean>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			File file = new File(filePath);
			return new RPCResult<Boolean>(true, file.isFile());
		}
		return new RPCResult<Boolean>(false, "Permission denied");
	}

	@Override
	public RPCResult<List<String>> getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<List<String>>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<List<String>>(false, "Authentication failed");
	
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<List<String>>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			List<String> resultList = new LinkedList<String>();
			File file = new File(filePath);
			for(File child : file.listFiles())
				if(child.isFile())
					resultList.add(child.getName());
			return new RPCResult<List<String>>(true, resultList);
		}
		return new RPCResult<List<String>>(false, "Permission denied");
		
		
	}

	@Override
	public RPCResult<Void> createFile(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		File parentFile = new File(filePath).getParentFile();
		if(parentFile == null)
			return new RPCResult<Void>(false, "Invalid destination");
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, parentFile.getAbsolutePath());
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			File file = new File(filePath);
			try {
				boolean createResult = file.createNewFile();
				return new RPCResult<Void>(createResult, createResult ? "" : "creating file failed");
			} catch (IOException e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "creating file failed");
			}
		}
		return new RPCResult<Void>(false, "Permission denied");
	}

	@Override
	public RPCResult<Integer> getDepth(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Integer>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Integer>(false, "Authentication failed");

		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Integer>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			File file = new File(filePath);
			int depth = this.getDepth(file);
			return new RPCResult<Integer>(true, depth);
		}
		return new RPCResult<Integer>(false, "Permission denied");
	}
	
	private int getDepth(File file) {
		int maxDepth = 0;
		for(File child : file.listFiles()) {
			if(child.isDirectory()) {
				int depth = getDepth(child) + 1;
				if(depth > maxDepth)
					maxDepth = depth;
			}
		}
		return maxDepth;
	}

	@Override
	public RPCResult<Void> zipDirectory(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			Zipper zipper = new Zipper();
			try {
				zipper.zip(filePath);
				return new RPCResult<Void>(true);
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Zipping failed");
			}
		}
		return new RPCResult<Void>(false, "Permission denied");
	}

	@Override
	public RPCResult<Void> setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Void>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Void>(false, "Authentication failed");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Void>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			try {
				FilesInUseDAO.getInstance().setInUse(value, filePath, task);
				return new RPCResult<Void>(true);
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Zipping failed");
			}		
		}
		return new RPCResult<Void>(false, "Permission denied");
	}

	@Override
	public RPCResult<Boolean> isInUse(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<Boolean>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			try {
				return new RPCResult<Boolean>(true, FilesInUseDAO.getInstance().isInUse(filePath));
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<Boolean>(false, "Zipping failed");
			}
		}
		return new RPCResult<Boolean>(false, "Permission denied");
	}

	@Override
	public RPCResult<List<Task>> getUsingTasks(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<List<Task>>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<List<Task>>(false, "Authentication failed");
		
		RPCResult<Boolean> permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult.isSucceeded())
			return new RPCResult<List<Task>>(false, permissionResult.getMessage());
		if(permissionResult.getData()) {
			try {
				return new RPCResult<List<Task>>(true, FilesInUseDAO.getInstance().getUsingTasks(filePath));
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<List<Task>>(false, "Internal Server Error");
			}
		}
		return new RPCResult<List<Task>>(false, "Permission denied");
	}
	
}

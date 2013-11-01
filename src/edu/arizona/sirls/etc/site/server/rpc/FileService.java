package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.Files;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
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
		RPCResult<Tree<FileInfo>> result = new RPCResult<Tree<FileInfo>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			Tree<FileInfo> resultTree = new Tree<FileInfo>(new FileInfo("", FileType.DIRECTORY));
			Tree<FileInfo> ownedFiles = new Tree<FileInfo>(new FileInfo("Owned", FileType.DIRECTORY));
			Tree<FileInfo> sharedFiles = new Tree<FileInfo>(new FileInfo("Shared", FileType.DIRECTORY));
			resultTree.addChild(ownedFiles);
			resultTree.addChild(sharedFiles);
			decorateOwnedTree(authenticationToken, ownedFiles, fileFilter, Configuration.fileBase + "//" + authenticationToken.getUsername());
			decorateSharedTree(authenticationToken, sharedFiles, fileFilter);
			
			return new RPCResult<Tree<FileInfo>>(true, resultTree);
		} 
		return result;
	}
	
	private void decorateSharedTree(AuthenticationToken authenticationToken, Tree<FileInfo> sharedFiles, FileFilter fileFilter) {
		try {
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
						inputTree.addChild(new Tree<FileInfo>(new FileInfo(file.getName(), getFileType(authenticationToken, file))));
					}
					Tree<FileInfo> outputTree = new Tree<FileInfo>(new FileInfo(share.getTask().getName(), FileType.DIRECTORY));
					shareTree.addChild(outputTree);
					for(String output : outputFiles) {
						File file = new File(output);
						outputTree.addChild(new Tree<FileInfo>(new FileInfo(file.getName(), getFileType(authenticationToken, file))));
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void decorateOwnedTree(AuthenticationToken authenticationToken, Tree<FileInfo> fileTree, FileFilter fileFilter, String filePath) {
		File file = new File(filePath);
		for(File child : file.listFiles()) {
			String name = child.getName();
			FileType fileType = getFileType(authenticationToken, child);
			if(fileType != null && !filter(fileType, fileFilter)) {
				Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(name, fileType));
				fileTree.addChild(childTree);
				if(child.isDirectory()) {
					decorateOwnedTree(authenticationToken, childTree, fileFilter, child.getAbsolutePath());
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

	private FileType getFileType(AuthenticationToken authenticationToken, File child) {
		String target = getTarget(authenticationToken, child);
		if(child.isFile()) {
			RPCResult<Boolean> validationResult = fileFormatService.isValidEuler(authenticationToken, target);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.EULER;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, target);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.GLOSSARY;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, target);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.GLOSSARY;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, target);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.TAXON_DESCRIPTION;
			validationResult = fileFormatService.isValidGlossary(authenticationToken, target);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileType.MARKEDUP_TAXON_DESCRIPTION;
		} else if (child.isDirectory())
			return FileType.DIRECTORY;
		return null;
	}

	private String getTarget(AuthenticationToken authenticationToke, File child) {
		String absolutePath = child.getAbsolutePath();
		absolutePath = absolutePath.replaceAll("\\" + File.separator, "//");
		return absolutePath.replace(Configuration.fileBase + "//" + authenticationToke.getUsername() + "//", "");
	}

	@Override
	public RPCResult<Boolean> deleteFile(AuthenticationToken authenticationToken, String target) {
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			if(!target.trim().isEmpty()) {
				RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, target);
				if(inUseResult.isSucceeded() && !inUseResult.getData()) {
					File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
					boolean resultDelete = deleteRecursively(file);
					result = new RPCResult<Boolean>(true, resultDelete);
				} else {
					result = createMessageFileInUse(authenticationToken, target);
				}
			} else {
				result = new RPCResult<Boolean>(false, "Target can not be empty");
			}
		}
		return result;
	}
	
	private RPCResult<Boolean> createMessageFileInUse(AuthenticationToken authenticationToken, String target) {
		RPCResult<List<Task>> tasksResult = this.getUsingTasks(authenticationToken, target);
		if(tasksResult.isSucceeded()) {
			List<Task> tasks = tasksResult.getData();
			StringBuilder messageBuilder = new StringBuilder("File is in use by tasks: ");
			for(Task task : tasks) 
				messageBuilder.append(task.getName() + ", ");
			String message = messageBuilder.toString();
			return new RPCResult<Boolean>(true, message.substring(0, message.length() - 2));
		}
		return new RPCResult<Boolean>(false, "Couldn't retrieve tasks using the file");
	}

	private boolean deleteRecursively(File file) {
		boolean result = false;
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				result = file.delete();
			} else {
				String files[] = file.list();
				for (String child : files) {
					File fileDelete = new File(file, child);
					result |= deleteRecursively(fileDelete);
				}
				if (file.list().length == 0) {
					result |= file.delete();
				}
			}
		} else {
			result = file.delete();
		}
		return result;
	}

	@Override
	public RPCResult<Boolean> moveFile(AuthenticationToken authenticationToken, String target, String newTarget) { 
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			if(!target.trim().isEmpty()) {
				if(!newTarget.trim().isEmpty() ) {
					RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, target);
					if(inUseResult.isSucceeded() && !inUseResult.getData()) {
						inUseResult = this.isInUse(authenticationToken, newTarget);
						if(inUseResult.isSucceeded() && !inUseResult.getData()) {
							File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
							File targetFile = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + newTarget);
							if(file.getAbsolutePath().equals(targetFile.getAbsolutePath()))
								return new RPCResult<Boolean>(true);
							if(targetFile.exists())
								return new RPCResult<Boolean>(false, "File does not exist");
							try {
								Files.move(file, targetFile);
								result = new RPCResult<Boolean>(true, true);
							} catch (IOException e) {
								e.printStackTrace();
								result = new RPCResult<Boolean>(false, "Couldn't move a file");
							}
						} else {
							result = createMessageFileInUse(authenticationToken, newTarget);
						}
					} else {
						result = createMessageFileInUse(authenticationToken, target);
					}
				} else {
					result = new RPCResult<Boolean>(false, "New target can not be empty");
				}
			} else {
				result = new RPCResult<Boolean>(false, "Target can not be empty");
			}
		}
		return result;
	}
	
	@Override
	public RPCResult<Boolean> createDirectory(AuthenticationToken authenticationToken, String target, String directoryName) { 
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) {
			if(!directoryName.trim().isEmpty()) {
				RPCResult<Boolean> inUseResult = this.isInUse(authenticationToken, target);
				if(inUseResult.isSucceeded() && !inUseResult.getData()) {
					File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target + "//" + directoryName);
					boolean resultMkDir = file.mkdir();
					result = new RPCResult<Boolean>(true, resultMkDir);
				} else {
					result = createMessageFileInUse(authenticationToken, target);
				}
			} else {
				result = new RPCResult<Boolean>(false, "Directory name can not be empty");
			}
		}
		return result;
	}

	@Override
	public RPCResult<Boolean> isDirectory(AuthenticationToken authenticationToken, String target) {
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return new RPCResult<Boolean>(true, file.isDirectory());
		}
		return result;
	}

	@Override
	public RPCResult<Boolean> isFile(AuthenticationToken authenticationToken, String target) {
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return new RPCResult<Boolean>(true, file.isFile());
		}
		return result;
	}

	@Override
	public RPCResult<List<String>> getDirectoriesFiles(AuthenticationToken authenticationToken, String target) {
		RPCResult<List<String>> result = new RPCResult<List<String>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			List<String> resultList = new LinkedList<String>();
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			for(File child : file.listFiles())
				if(child.isFile())
					resultList.add(child.getName());
			return new RPCResult<List<String>>(true, resultList);
		}
		return result;
	}

	@Override
	public RPCResult<Void> createFile(AuthenticationToken authenticationToken, String target) {
		RPCResult<Void> result = new RPCResult<Void>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			try {
				boolean createResult = file.createNewFile();
				return new RPCResult<Void>(createResult, createResult ? "" : "creating file failed");
			} catch (IOException e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "creating file failed");
			}
		}
		return result;
	}

	@Override
	public RPCResult<Integer> getDepth(AuthenticationToken authenticationToken, String target) {
		RPCResult<Integer> result = new RPCResult<Integer>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			int depth = this.getDepth(file);
			return new RPCResult<Integer>(true, depth);
		}
		return result;
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
	public RPCResult<Void> zipDirectory(AuthenticationToken authenticationToken, String target) {
		RPCResult<Void> result = new RPCResult<Void>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			Zipper zipper = new Zipper();
			try {
				zipper.zip(authenticationToken.getUsername(), target);
				return new RPCResult<Void>(true);
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Zipping failed");
			}
		}
		return result;
	}

	@Override
	public RPCResult<Void> setInUse(AuthenticationToken authenticationToken, boolean value, String target, Task task) {
		RPCResult<Void> result = new RPCResult<Void>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				FilesInUseDAO.getInstance().setInUse(value, target, task);
				return new RPCResult<Void>(true);
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Zipping failed");
			}
		}
		return result;
	}

	@Override
	public RPCResult<Boolean> isInUse(AuthenticationToken authenticationToken, String target) {
		RPCResult<Boolean> result = new RPCResult<Boolean>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				return new RPCResult<Boolean>(true, FilesInUseDAO.getInstance().isInUse(target));
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<Boolean>(false, "Zipping failed");
			}
		}
		return result;
	}

	@Override
	public RPCResult<List<Task>> getUsingTasks(AuthenticationToken authenticationToken, String target) {
		RPCResult<List<Task>> result = new RPCResult<List<Task>>(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			try {
				return new RPCResult<List<Task>>(true, FilesInUseDAO.getInstance().getUsingTasks(target));
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<List<Task>>(false, "Internal Server Error");
			}
		}
		return result;
	}
	
}

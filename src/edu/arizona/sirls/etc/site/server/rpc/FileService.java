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
import edu.arizona.sirls.etc.site.shared.rpc.MessageResult;
import edu.arizona.sirls.etc.site.shared.rpc.Tree;
import edu.arizona.sirls.etc.site.shared.rpc.db.FilesInUseDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
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
	public Tree<FileInfo> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter) {
		//will become part of 'target' and has effect on several things. therefore leave root as "" for now. Also will have to escape \\'s before it is inserted into SQL statements
		//sql injection..
		//Tree<FileInfo> result = new Tree<FileInfo>(new FileInfo(authenticationToken.getUsername() + "'s files", FileType.DIRECTORY));
		Tree<FileInfo> result = new Tree<FileInfo>(new FileInfo("", FileType.DIRECTORY));
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			decorateTree(authenticationToken, result, fileFilter, Configuration.fileBase + "//" + authenticationToken.getUsername());
		} 
		return result;
	}
	
	private void decorateTree(AuthenticationToken authenticationToken, Tree<FileInfo> fileTree, FileFilter fileFilter, String filePath) {
		File file = new File(filePath);
		for(File child : file.listFiles()) {
			String name = child.getName();
			FileType fileType = getFileType(authenticationToken, child);
			if(!filter(fileType, fileFilter)) {
				Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(name, fileType));
				fileTree.addChild(childTree);
				if(child.isDirectory()) {
					decorateTree(authenticationToken, childTree, fileFilter, child.getAbsolutePath());
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
			if(fileFormatService.isValidEuler(authenticationToken, target))
				return FileType.EULER;
			if(fileFormatService.isValidGlossary(authenticationToken, target))
				return FileType.GLOSSARY;
			if(fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, target))
				return FileType.GLOSSARY;
			if(fileFormatService.isValidTaxonDescription(authenticationToken, target))
				return FileType.TAXON_DESCRIPTION;
			if(fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, target))
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
	public MessageResult deleteFile(AuthenticationToken authenticationToken, String target) {
		MessageResult result = new MessageResult(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			if(!target.trim().isEmpty()) {
				if(!this.isInUse(authenticationToken, target)) {
					File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
					boolean resultDelete = deleteRecursively(file);
					result = new MessageResult(resultDelete, resultDelete? "" : "Couldn't delete a file");
				} else {
					result = createMessageFileInUse(authenticationToken, target);
				}
			} else {
				result = new MessageResult(false, "Target can not be empty");
			}
		}
		return result;
	}
	
	private MessageResult createMessageFileInUse(AuthenticationToken authenticationToken, String target) {
		List<Task> tasks = this.getUsingTasks(authenticationToken, target);
		StringBuilder messageBuilder = new StringBuilder("File is in use by tasks: ");
		for(Task task : tasks) 
			messageBuilder.append(task.getName() + ", ");
		String message = messageBuilder.toString();
		return new MessageResult(false, message.substring(0, message.length() - 2));
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
	public MessageResult moveFile(AuthenticationToken authenticationToken, String target, String newTarget) { 
		MessageResult result = new MessageResult(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			if(!target.trim().isEmpty()) {
				if(!newTarget.trim().isEmpty() ) {
					if(!this.isInUse(authenticationToken, target)) {
						if(!this.isInUse(authenticationToken, newTarget)) {
							File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
							File targetFile = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + newTarget);
							if(file.getAbsolutePath().equals(targetFile.getAbsolutePath()))
								return new MessageResult(true);
							if(targetFile.exists())
								return new MessageResult(false, "File does not exist");
							try {
								Files.move(file, targetFile);
								result = new MessageResult(true);
							} catch (IOException e) {
								e.printStackTrace();
								result = new MessageResult(false, "Couldn't move a file");
							}
						} else {
							result = createMessageFileInUse(authenticationToken, newTarget);
						}
					} else {
						result = createMessageFileInUse(authenticationToken, target);
					}
				} else {
					result = new MessageResult(false, "New target can not be empty");
				}
			} else {
				result = new MessageResult(false, "Target can not be empty");
			}
		}
		return result;
	}
	
	@Override
	public MessageResult createDirectory(AuthenticationToken authenticationToken, String target, String directoryName) { 
		MessageResult result = new MessageResult(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			if(!directoryName.trim().isEmpty()) {
				if(!this.isInUse(authenticationToken, target)) {
					File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target + "//" + directoryName);
					boolean resultMkDir = file.mkdir();
					result = new MessageResult(resultMkDir, resultMkDir ? "" : "Couldn't create directory");
				} else {
					result = createMessageFileInUse(authenticationToken, target);
				}
			} else {
				result = new MessageResult(false, "Directory name can not be empty");
			}
		}
		return result;
	}

	@Override
	public boolean isDirectory(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return file.isDirectory();
		}
		return false;
	}

	@Override
	public boolean isFile(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return file.isFile();
		}
		return false;
	}

	@Override
	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String target) {
		List<String> result = new LinkedList<String>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			for(File child : file.listFiles())
				if(child.isFile())
					result.add(child.getName());
			return result;
		}
		return result;
	}

	@Override
	public MessageResult createFile(AuthenticationToken authenticationToken, String target) {
		MessageResult result = new MessageResult(false, "Authentication failed");
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			try {
				boolean createResult = file.createNewFile();
				return new MessageResult(createResult, createResult ? "" : "creating file failed");
			} catch (IOException e) {
				e.printStackTrace();
				return new MessageResult(false, "creating file failed");
			}
		}
		return result;
	}

	@Override
	public Integer getDepth(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			return this.getDepth(file);
		}
		return -1;
	}
	
	private Integer getDepth(File file) {
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
	public void zipDirectory(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			Zipper zipper = new Zipper();
			try {
				zipper.zip(authenticationToken.getUsername(), target);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setInUse(AuthenticationToken authenticationToken, boolean value, String target, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				FilesInUseDAO.getInstance().setInUse(value, target, task);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isInUse(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				return FilesInUseDAO.getInstance().isInUse(target);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public List<Task> getUsingTasks(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				
				return FilesInUseDAO.getInstance().getUsingTasks(target);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return new LinkedList<Task>();
	}
	
}

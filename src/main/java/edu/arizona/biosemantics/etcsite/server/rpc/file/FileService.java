package edu.arizona.biosemantics.etcsite.server.rpc.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.validation.key.KeyElementValidator;
import edu.arizona.biosemantics.common.validation.key.KeyValidationException;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.FilesInUseDAO;
import edu.arizona.biosemantics.etcsite.server.db.ShareDAO;
import edu.arizona.biosemantics.etcsite.server.db.TasksOutputFilesDAO;
import edu.arizona.biosemantics.etcsite.server.db.UserDAO;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.Tree;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateFileFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.FileDeleteFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.MoveFileFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.RenameFileFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.ZipDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskService;

public class FileService extends RemoteServiceServlet implements IFileService {

	private static final long serialVersionUID = -9193602268703418530L;
	private IFilePermissionService filePermissionService;
	@SuppressWarnings("unused")
	private IFileFormatService fileFormatService;
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy");
	private	XmlNamespaceManager xmlNamespaceManager;
	private FileNameNormalizer fileNameNormalizer;
	private ITaskService taskService;
	private DAOManager daoManager;
	
	@Inject
	public FileService(IFileFormatService fileFormatService, IFilePermissionService filePermissionService, ITaskService taskService, 
			DAOManager daoManager, FileNameNormalizer fileNameNormalizer, XmlNamespaceManager xmlNamespaceManager) {
		this.fileFormatService = fileFormatService;
		this.filePermissionService = filePermissionService;
		this.taskService = taskService;
		this.daoManager = daoManager;
		this.fileNameNormalizer = fileNameNormalizer;
		this.xmlNamespaceManager = xmlNamespaceManager;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	/**
	 * Cannot hand a fileFilter implementation of .filter(File file) to the method because
	 * file access is not available in the client code. Hence, translation from enum to implementation 
	 * becomes necessary.
	 * @throws PermissionDeniedException 
	 */
	@Override
	public Tree<FileInfo> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter) throws PermissionDeniedException {
		//will become part of 'target' and has effect on several things. therefore leave root as "" for now. Also will have to escape \\'s before it is inserted into SQL statements
		//sql injection..
		//Tree<FileInfo> result = new Tree<FileInfo>(new FileInfo(authenticationToken.getUsername() + "'s files", FileType.DIRECTORY));
		
		Tree<FileInfo> resultTree = new Tree<FileInfo>(new FileInfo("", "Root", "", FileTypeEnum.DIRECTORY, authenticationToken.getUserId(), true, false, false));
		Tree<FileInfo> ownedFiles = new Tree<FileInfo>(new FileInfo("Owned", Configuration.fileBase + File.separator + authenticationToken.getUserId(),
				"Owned", FileTypeEnum.DIRECTORY, authenticationToken.getUserId(), true, false, true));
		Tree<FileInfo> sharedFiles = new Tree<FileInfo>(new FileInfo("Shared", "Shared", "Shared", FileTypeEnum.DIRECTORY, authenticationToken.getUserId(), true, false, false));
		resultTree.addChild(ownedFiles);
		resultTree.addChild(sharedFiles);
		
		decorateOwnedTree(authenticationToken, ownedFiles, fileFilter, Configuration.fileBase + File.separator + authenticationToken.getUserId());
		decorateSharedTree(authenticationToken, sharedFiles, fileFilter);
		return resultTree;
	}
	
	private void decorateSharedTree(AuthenticationToken authenticationToken, Tree<FileInfo> sharedFiles, FileFilter fileFilter) 
			throws PermissionDeniedException {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Share> shares = daoManager.getShareDAO().getSharesOfInvitee(user);
		
		for(Share share : shares) {
			int shareOwnerUserId = share.getTask().getUser().getId();
			Tree<FileInfo> shareTree = new Tree<FileInfo>(new FileInfo(share.getTask().getName(), "Share." + share.getTask().getId(), 
					share.getTask().getName(), FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false));
			sharedFiles.addChild(shareTree);
			AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
			
			List<String> outputs = daoManager.getTasksOutputFilesDAO().getOutputs(share.getTask());
			Tree<FileInfo> outputTree = new Tree<FileInfo>(new FileInfo("Output", "Share.Output." + share.getTask().getId(), 
					"Output", FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false));
			shareTree.addChild(outputTree);
			for(String output : outputs) {
				File child = new File(output);
				if(child.exists()) {
					boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, output);
					if(!permissionResult)
						throw new PermissionDeniedException();
					else {
						String displayPath = share.getTask().getName() + File.separator + "Output" + File.separator + child.getName();
						Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(child.getName(), child.getAbsolutePath(), displayPath, 
								getFileType(authenticationToken, output), shareOwnerUserId, false, false, false));
						outputTree.addChild(childTree);
						if(child.isDirectory()) {
							decorateOwnedTree(authenticationToken, childTree, fileFilter, child.getAbsolutePath());
						}
					}
				}
			}
			
			if(taskConfiguration != null) {
				List<String> inputFiles = taskConfiguration.getInputs();
				Tree<FileInfo> inputTree = new Tree<FileInfo>(new FileInfo("Input", "Share.Input." + share.getTask().getId(), "Input", 
						FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false));
				shareTree.addChild(inputTree);
				for(String input : inputFiles) {
					File child = new File(input);
					if(child.exists()) {
						Boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, input);
						if(!permissionResult)
							throw new PermissionDeniedException();
						else {
							String displayPath = share.getTask().getName() + File.separator + "Input" + File.separator + child.getName();
							Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(child.getName(), child.getAbsolutePath(), displayPath, 
									getFileType(authenticationToken, input), shareOwnerUserId, false, false, false));
							inputTree.addChild(childTree);
							if(child.isDirectory()) {
								decorateOwnedTree(authenticationToken, childTree, fileFilter, child.getAbsolutePath());
							}
						}
					}
				}
			}
		}
	}

	private void decorateOwnedTree(AuthenticationToken authenticationToken, Tree<FileInfo> fileTree, FileFilter fileFilter, String filePath) throws PermissionDeniedException {
		Boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(filePath);
			File[] childFiles = file.listFiles();
			Arrays.sort(childFiles);
			for(File child : childFiles) {
				String childPath = child.getAbsolutePath();
				permissionResult = filePermissionService.hasReadPermission(authenticationToken, childPath);
				if (!permissionResult)
					throw new PermissionDeniedException();
				String name = child.getName();

				FileTypeEnum fileType = null;
				if (fileFilter != null) { // Call getFileType only if we have not received a null fileFilter, since it is slow.
					fileType = getFileType(authenticationToken,
							child.getAbsolutePath());
					if (fileType == null || filter(fileType, fileFilter)) {
						continue;
					}
				}

				if (fileType == null) // Indicates that function received a null fileFilter. 
					fileType = child.isDirectory() ? FileTypeEnum.DIRECTORY : FileTypeEnum.PLAIN_TEXT;

				String displayPath = childPath.replace(Configuration.fileBase
						+ File.separator + authenticationToken.getUserId(), "");
				Tree<FileInfo> childTree = new Tree<FileInfo>(new FileInfo(
						name, child.getAbsolutePath(), displayPath, fileType,
						authenticationToken.getUserId(), false,
						child.isDirectory(), true));
				fileTree.addChild(childTree);
				if (child.isDirectory()) {
					decorateOwnedTree(authenticationToken, childTree,
							fileFilter, childPath);
				}
			}
		}
	}

	private boolean filter(FileTypeEnum fileType, FileFilter fileFilter) {
		switch(fileFilter) {
		case TAXON_DESCRIPTION:
			return !fileType.equals(FileTypeEnum.TAXON_DESCRIPTION);
		case MARKED_UP_TAXON_DESCRIPTION:
			return !fileType.equals(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION);	
		case MATRIX:
			return !fileType.equals(FileTypeEnum.MATRIX);	
		case ALL:
			return false;
		case FILE:
			return fileType.equals(FileTypeEnum.DIRECTORY);
		case DIRECTORY:
			return !fileType.equals(FileTypeEnum.DIRECTORY);
		case OWL_ONTOLOGY:
			return !fileType.equals(FileTypeEnum.OWL_ONTOLOGY);
		}
		
		return true;
	}

	private FileTypeEnum getFileType(AuthenticationToken authenticationToken, String filePath) {
		File file = new File(filePath);
		if(file.isFile()) {
			if(file.getName().endsWith(".xml")) {
				try {
					return xmlNamespaceManager.getFileType(new File(filePath));
				} catch(Exception e) {
					log(LogLevel.ERROR, "Couldn't get file type", e);
					return null;
				}
			} else if(file.getName().endsWith(".csv") || file.getName().endsWith(".mx") || file.getName().endsWith(".nxs") || file.getName().endsWith(".nex") || file.getName().endsWith(".sdd")) {
				return FileTypeEnum.MATRIX;
			} else if(file.getName().endsWith(".owl")) {
				return FileTypeEnum.OWL_ONTOLOGY;
			} else 
				return FileTypeEnum.PLAIN_TEXT;
			/*RPCResult<Boolean> validationResult = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION;
			validationResult = fileFormatService.isValidTaxonDescription(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileTypeEnum.TAXON_DESCRIPTION;
			*/
			//return FileTypeEnum.PLAIN_TEXT;
			/*validationResult = fileFormatService.isValidMatrix(authenticationToken, filePath);
			if(validationResult.isSucceeded() && validationResult.getData())
				return FileTypeEnum.MATRIX;*/
		} else if (file.isDirectory())
			return FileTypeEnum.DIRECTORY;
		return null;
	}

	/**
	 * if delete successfully, leave result message blank "".
	 * otherwise, pass the error in result message.
	 * @throws PermissionDeniedException,  
	 */
	@Override
	public void deleteFile(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, 
			FileDeleteFailedException {
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			boolean inUseResult = this.isInUse(authenticationToken, filePath);
			if(!inUseResult) {
				File file = new File(filePath);
				boolean resultDelete = deleteRecursively(authenticationToken, file);
				if(!resultDelete)
					throw new FileDeleteFailedException();
			} else {
				throw new FileDeleteFailedException("Can't delete file, it "+this.createMessageFileInUse(authenticationToken, filePath)+ ". Delete the tasks using Task Manager, then try again.");
			}
		}
	}
	
	private String createMessageFileInUse(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		List<Task> tasks = this.getUsingTasks(authenticationToken, filePath);
		StringBuilder messageBuilder = new StringBuilder("is in use by task(s): ");
		for(Task task : tasks) 
			messageBuilder.append(task.getName() + ", ");
		String message = messageBuilder.toString();
		return message.substring(0, message.length() - 2);
	}

	private boolean deleteRecursively(AuthenticationToken authenticationToken, File file) throws PermissionDeniedException {
		boolean result = false;
		
		String filePath = file.getAbsolutePath();
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			if(file.exists()) {
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
			} else {
				return true;
			}
		}
		return result;
	}
	
	/**
	 * create directory successfully: message="", result = folder created
	 * failed to create directory: message=error, result = "";
	 * @throws PermissionDeniedException 
	 * @throws FileInUseException 
	 */
	@Override
	public String createDirectory(AuthenticationToken authenticationToken, String filePath, String idealFolderName, boolean force) 
			throws PermissionDeniedException, CreateDirectoryFailedException { 
		if(idealFolderName.trim().isEmpty()) 
			throw new CreateDirectoryFailedException("Directory name is empty. The directory was not created.");
		
		idealFolderName = fileNameNormalizer.normalize(idealFolderName);
		
		File dir = new File(filePath);
		dir.mkdirs();
		
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			boolean inUseResult = this.isInUse(authenticationToken, filePath);
			if(inUseResult)
				//throw new CreateDirectoryFailedException("Can't create directory. Parent directory is currently in use.");
				throw new CreateDirectoryFailedException("Can't create directory. Parent directory "+this.createMessageFileInUse(authenticationToken, filePath)+". Delete the tasks using Task Manager, then try again.");
			else {
				File file = new File(filePath + File.separator + idealFolderName);
				boolean resultMkDir = file.mkdir();
				if(!resultMkDir && force) {
					String date = dateTimeFormat.format(new Date());
					idealFolderName = idealFolderName + "_" + date;
					file = new File(filePath + File.separator + idealFolderName);
					resultMkDir = file.mkdir();
					int i = 1;
					while(!resultMkDir) {
						file = new File(filePath + File.separator + idealFolderName + "_" + i++);
						resultMkDir = file.mkdir();
					}
				}
				if(!resultMkDir){ 
					if(file.isDirectory() && file.exists())
						throw new CreateDirectoryFailedException("Directory "+file.getName()+" already exists. You can try again with another name.");
					else
						throw new CreateDirectoryFailedException("Network or server errors");
				}
				else
					return file.getAbsolutePath();
			}
		}
	}

	/**
	 * create file successfully: message="", result = file created
	 * failed to create file: message=error, result = "";
	 * @throws CreateFileFailedException 
	 */
	@Override
	public String createFile(AuthenticationToken authenticationToken, String directory, String idealFileName, boolean force) 
			throws CreateFileFailedException, PermissionDeniedException {					
		idealFileName = fileNameNormalizer.normalize(idealFileName);
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, directory);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(directory + File.separator + idealFileName);
				boolean createResult = false;
				try {
					createResult = file.createNewFile();
				} catch (IOException e) {
					log(LogLevel.ERROR, "Create new file failed", e);
					throw new CreateFileFailedException();
				}
				int i = 1;
				while(!createResult && force) {
					String namePart = idealFileName;
					if(idealFileName.lastIndexOf(".") != -1) {
						namePart = idealFileName.substring(0, idealFileName.lastIndexOf("."));
					}
					file = new File(directory + File.separator + namePart + "_" + i++ + "." + 
							idealFileName.substring(idealFileName.lastIndexOf(".") + 1, idealFileName.length()));
					try {
						createResult = file.createNewFile();
					} catch (IOException e) {
						log(LogLevel.ERROR, "Create new file failed", e);
						throw new CreateFileFailedException();
					}
				}
				if(createResult)
					return file.getAbsolutePath();
				else
					throw new CreateFileFailedException();
		}
	}
	
	@Override
	public boolean isDirectory(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			if(filePath.startsWith("Share.") || filePath.equals("Root") || filePath.equals("Owned") || filePath.equals("Shared"))
				return true;
			File file = new File(filePath);
			return file.isDirectory();
		}
	}

	@Override
	public boolean isFile(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(filePath);
			return file.isFile();
		}
	}

	@Override
	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {	
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			List<String> resultList = new LinkedList<String>();
			File file = new File(filePath);
			File[] childFiles = file.listFiles();
			Arrays.sort(childFiles);
			for(File child : childFiles)
				if(child.isFile())
					resultList.add(child.getName());
			return resultList;
		}
	}

	@Override
	public Integer getDepth(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(filePath);
			return this.getDepth(file);
		}
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
	public String zipDirectory(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, ZipDirectoryFailedException {	
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		DirectoryDownload directoryDownload = new DirectoryDownload(authenticationToken, filePath, this, 
				filePermissionService, daoManager);
		boolean result;
		try {
			result = directoryDownload.execute();
		} catch (CopyFilesFailedException | FileDeleteFailedException
				| CreateDirectoryFailedException e) {
			throw new ZipDirectoryFailedException();
		}
		if(result)
			return directoryDownload.getZipPath();
		else 
			throw new ZipDirectoryFailedException(directoryDownload.getError());
	}

	@Override
	public void setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task) throws PermissionDeniedException {		
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			daoManager.getFilesInUseDAO().setInUse(value, filePath, task);
		}
	}

	@Override
	public boolean isInUse(AuthenticationToken authenticationToken, String filePath) {	
		return daoManager.getFilesInUseDAO().isInUse(filePath);
	}
	
	@Override
	public List<Task> getUsingTasks(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			return daoManager.getFilesInUseDAO().getUsingTasks(filePath);
		}
	}

	@Override
	public void renameFile(AuthenticationToken authenticationToken, String path, String newFileName) throws 
			RenameFileFailedException, PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, path);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			boolean inUseResult = this.isInUse(authenticationToken, path);
			if(!inUseResult) {
				File file = new File(path);
				File newFile = new File(file.getParent() + File.separator + newFileName);
				if(newFile.exists()) 
					throw new RenameFileFailedException("Filename already exists");
				inUseResult = this.isInUse(authenticationToken, newFile.getAbsolutePath());
				if(!inUseResult) {
					if(file.getAbsolutePath().equals(newFile.getAbsolutePath()))
						return;
						if(file.isFile())
							try {
								FileUtils.moveFile(file, newFile);
							} catch (IOException e) {
								log(LogLevel.ERROR, "Couldn't move file", e);
								throw new RenameFileFailedException();
							}
						else
							try {
								FileUtils.moveDirectory(file, newFile);
							} catch (IOException e) {
								log(LogLevel.ERROR, "Couldn't move directory", e);
								throw new RenameFileFailedException();
							}
						//Files.move(file, newFile);
						return;
				} else {
					throw new RenameFileFailedException("Can not rename file, it "+createMessageFileInUse(authenticationToken, path)+". Delete the tasks using Task Manager, then try again.");
				}
			} else {
				throw new RenameFileFailedException("Can not rename file, it "+createMessageFileInUse(authenticationToken, path)+". Delete the tasks using Task Manager, then try again.");
			}
		}
	}
	
	@Override
	public void moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath) throws MoveFileFailedException, PermissionDeniedException { 
		boolean permissionResult = filePermissionService.hasWritePermission(authenticationToken, filePath);
		File newDirectory = new File(newFilePath).getParentFile();
		if(newDirectory == null) 
			throw new MoveFileFailedException("Invalid destination");
		boolean permissionResultNewDirectory = filePermissionService.hasWritePermission(authenticationToken, newDirectory.getAbsolutePath());
		if(permissionResult && permissionResultNewDirectory) {
			boolean inUseResult = this.isInUse(authenticationToken, filePath);
			if(!inUseResult) {
				inUseResult = this.isInUse(authenticationToken, newFilePath);
				if(!inUseResult) {
					File file = new File(filePath);
					File newFile = new File(newFilePath);
					if(file.getAbsolutePath().equals(newFile.getAbsolutePath()))
						return;
					if(!newFile.exists())
						throw new MoveFileFailedException("Destination directory does not exist");
					try {
						FileUtils.moveToDirectory(file, newFile, false);
						//Files.move(file, newFile);
						return;
					} catch (IOException e) {
						String message = "Couldn't move file";
						log(LogLevel.ERROR, message, e);
						throw new MoveFileFailedException(message);
					}
				} else {
					throw new MoveFileFailedException("Can not move file(s), it "+createMessageFileInUse(authenticationToken, newFilePath)+". Delete the tasks using Task Manager, then try again. ");
				}
			} else {
				throw new MoveFileFailedException("Can not move file(s), it "+createMessageFileInUse(authenticationToken, filePath)+". Delete the tasks using Task Manager, then try again.");
			}
		}	
	}

	@Override
	public String getParent(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(filePath);
			File parentFile = file.getParentFile();
			return parentFile == null ? null : parentFile.getAbsolutePath();
		}
	}

	@Override
	public String getFileName(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		else {
			File file = new File(filePath);
			return file.getName();
		}
	}

	@Override
	public void copyFiles(AuthenticationToken authenticationToken, String source, String destination) throws CopyFilesFailedException, PermissionDeniedException {
		boolean permissionResultSource = filePermissionService.hasReadPermission(authenticationToken, source);
		boolean permissionResultDestination = filePermissionService.hasWritePermission(authenticationToken, destination);
		if(!permissionResultSource || !permissionResultDestination)
			throw new PermissionDeniedException();
		if(permissionResultSource && permissionResultDestination) {
			try {
				FileUtils.copyDirectory(new File(source), new File(destination));
			} catch(Exception e) {
				String message = "Couldn't copy directory";
				log(LogLevel.ERROR, message, e);
				throw new CopyFilesFailedException(message);
			} 
			/*File sourceDir = new File(source);
			for(File sourceFile : sourceDir.listFiles()) {
				File destinationFile = new File(destination, sourceFile.getName());
				try {
					
					Files.copy(sourceFile, destinationFile);
				} catch(Exception e) {
					e.printStackTrace();
					return new RPCResult<Void>(false, e.getMessage());
				}
			}*/
		}
	}	

	@Override
	public String getDownloadPath(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, ZipDirectoryFailedException {
		boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!permissionResult)
			throw new PermissionDeniedException();
		boolean isDirectory = this.isDirectory(authenticationToken, filePath);
		if(filePath.startsWith("Share.") || filePath.equals("Root") || filePath.equals("Owned") || filePath.equals("Shared") || 
				isDirectory) {
			String zipResult = this.zipDirectory(authenticationToken, filePath);
			return zipResult;
		} else {
			return filePath;
		}
	}


	@Override
	public HashMap<String, String> validateKeys(AuthenticationToken authenticationToken, String filePath, List<String> uploadedFiles) {
		HashMap<String,String> resultList = new HashMap<String, String>();
		KeyElementValidator validator = new KeyElementValidator();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for(File child : childFiles){
			if(child.isFile() && uploadedFiles.contains(child.getName())){
				try {
					validator.validate(child.getAbsolutePath());
				} catch (KeyValidationException e) {
					String allErrors = "";
					allErrors+="Errors in "+child.getName()+":\n";
					for(String error: e.getAllErrors()){
						allErrors+=error+"\n";
					}
					resultList.put(child.getAbsolutePath(), allErrors);
				}
			}				
		}	
		return resultList;
	}
	
	@Override
	public void deleteUploadedFiles(AuthenticationToken token, String uploadedDirectory, List<String> uploadedFiles) throws PermissionDeniedException, FileDeleteFailedException {
		// TODO Auto-generated method stub
		File file = new File(uploadedDirectory);
		File[] childFiles = file.listFiles();
		for(File child : childFiles){
			if(child.isFile() && uploadedFiles.contains(child.getName())){
				deleteFile(token, child.getAbsolutePath());
			}
		}	
	}

	@Override
	public List<FileInfo> getAllOwnedFolders(AuthenticationToken authenticationToken) {
		List<FileInfo> allOwnedFolders = new LinkedList<FileInfo>();
		File ownedFolder = new File(Configuration.fileBase + File.separator + authenticationToken.getUserId());
		File[] childFiles = ownedFolder.listFiles();
		for(File child : childFiles){
			String fileAbsolutePath = child.getAbsolutePath();
			String displayPath = fileAbsolutePath.replace(Configuration.fileBase + File.separator + authenticationToken.getUserId(), "");
			allOwnedFolders.add(new FileInfo(child.getName(), fileAbsolutePath, displayPath, getFileType(authenticationToken, fileAbsolutePath), 
					authenticationToken.getUserId(), false, child.isDirectory(), true));
		}
		return allOwnedFolders;
	}

	@Override
	public FileInfo getOwnedRootFolder(AuthenticationToken authenticationToken) {
		File ownedFolder = new File(Configuration.fileBase + File.separator + authenticationToken.getUserId());
		FileInfo ownedFolderFileInfo = new FileInfo(ownedFolder.getName(), ownedFolder.getAbsolutePath(), "", getFileType(authenticationToken, ownedFolder.getAbsolutePath()), authenticationToken.getUserId(), false, ownedFolder.isDirectory(), true);
		return ownedFolderFileInfo;
	}

	@Override
	public List<FileInfo> getAllSharedFolders(
			AuthenticationToken authenticationToken) {
		List<FileInfo> allSharedFolders = new LinkedList<FileInfo>();
		File sharedFolder = new File(Configuration.fileBase + File.separator + authenticationToken.getUserId());
		File[] childFiles = sharedFolder.listFiles();
		for(File child : childFiles){
			String fileAbsolutePath = child.getAbsolutePath();
			String displayPath = fileAbsolutePath.replace(Configuration.fileBase + File.separator + authenticationToken.getUserId(), "");
			allSharedFolders.add(new FileInfo(child.getName(), fileAbsolutePath, displayPath, getFileType(authenticationToken, fileAbsolutePath), 
					authenticationToken.getUserId(), false, child.isDirectory(), true));
		}
		return allSharedFolders;
	}

}


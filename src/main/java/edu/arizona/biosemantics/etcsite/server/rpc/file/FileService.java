package edu.arizona.biosemantics.etcsite.server.rpc.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.validation.key.KeyElementValidator;
import edu.arizona.biosemantics.common.validation.key.KeyValidationException;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.process.file.MatrixGenerationSerializedModelReader;
import edu.arizona.biosemantics.etcsite.server.process.file.TaxonNameValidator;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.CleanTaxReader;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Share;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
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
import edu.arizona.biosemantics.etcsite.shared.model.file.FileSource;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

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
		case CLEANTAX:
			return !fileType.equals(FileTypeEnum.CLEANTAX);
		case MATRIX_GENERATION_SERIALIZED_MODEL:
			return !fileType.equals(FileTypeEnum.MATRIX_GENERATION_SERIALIZED_MODEL);
		}
		
		return true;
	}

	private FileTypeEnum getFileType(AuthenticationToken authenticationToken, String filePath) {
		File file = new File(filePath);
		if(file.isFile()) {
			if(file.getName().endsWith(".ser")) {
				try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
					edu.arizona.biosemantics.matrixreview.shared.model.Model model = (edu.arizona.biosemantics.matrixreview.shared.model.Model)input.readObject();
					return FileTypeEnum.MATRIX_GENERATION_SERIALIZED_MODEL;
				} catch(Exception e) {
					
				}
			}
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
			} else {
				CleanTaxReader reader = new CleanTaxReader();
				try {
					boolean validCleanTax = reader.isValid(file);
					if(validCleanTax)
						return FileTypeEnum.CLEANTAX;
					else
						return FileTypeEnum.PLAIN_TEXT;
				} catch(IOException e) {
					log(LogLevel.ERROR, "Could not read file.", e);
					return FileTypeEnum.PLAIN_TEXT;
				}
			}
				
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
        if(dir.isFile()) {
        	dir = dir.getParentFile();
        	filePath = dir.getAbsolutePath();
        }
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
	public FolderTreeItem getOwnedRootFolder(AuthenticationToken token) {
		return createFolderTreeItem("Owned", "Owned", Configuration.fileBase + File.separator + token.getUserId(), "Owned", FileTypeEnum.DIRECTORY,
				token.getUserId(), true, false, true, FileSource.SYSTEM);
	}
	
	@Override
	public List<FileTreeItem> getFiles(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException {		
		if(folderTreeItem == null)
			return createRootFiles(authenticationToken, fileFilter);
		else if(folderTreeItem.getFilePath().equals("Shared"))
			return createSharedFolders(authenticationToken, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.Output."))
			return createSharedOutputTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.Input.")) 
			return createSharedInputTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.")) 
			return createSharedTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else 
			return createFilesByPath(authenticationToken, folderTreeItem, fileFilter);
	}

	private List<FileTreeItem> createSharedInputTaskFolder(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		int shareId = Integer.parseInt(folderTreeItem.getFilePath().replace("Share.Input.", ""));
		Share share = daoManager.getShareDAO().getShare(shareId);
		int shareOwnerUserId = share.getTask().getUser().getId();
		AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
		List<String> inputFiles = taskConfiguration.getInputs();
		for(String input : inputFiles) {
			File child = new File(input);
			if(child.exists()) {
				Boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, input);
				if(!permissionResult)
					throw new PermissionDeniedException();
				else {
					String displayPath = share.getTask().getName() + File.separator + "Input" + File.separator + child.getName();
	                boolean filter = false; 
	                FileTypeEnum fileType = FileTypeEnum.PLAIN_TEXT;
	                if(fileFilter != null) {
	                	fileType = getFileType(authenticationToken, child.getAbsolutePath());
	                	filter = filter(fileType, fileFilter);
	                }
	                	 
	                if(!filter) 
	                	if(child.isDirectory())
	                		result.add(createFolderTreeItem(child.getName(), getDisplayName(child), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false, FileSource.SHARED));
	                	else
	                		result.add(createFileTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false, FileSource.SHARED));						
				}
			}
		}
		return result;
	}

	private FileTreeItem createFileTreeItem(String name, String path, String displayPath, FileTypeEnum fileType, int ownerUserId, boolean isSystemFile, 
			boolean isAllowsNewFiles, boolean isAllowsNewFolders, FileSource fileSource) {
		return new FileTreeItem(UUID.randomUUID().toString(), name, name, path, displayPath, fileType, ownerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders, 
				fileSource); 
	}
	
	private FolderTreeItem createFolderTreeItem(String name, String displayName, String path, String displayPath, FileTypeEnum fileType, int ownerUserId, boolean isSystemFile, 
			boolean isAllowsNewFiles, boolean isAllowsNewFolders, FileSource fileSource) {
		return new FolderTreeItem(UUID.randomUUID().toString(), name, displayName, path, displayPath, fileType, ownerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders, 
				fileSource); 
	}

	private List<FileTreeItem> createSharedOutputTaskFolder(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		int shareId = Integer.parseInt(folderTreeItem.getFilePath().replace("Share.Output.", ""));
		Share share = daoManager.getShareDAO().getShare(shareId);
		int shareOwnerUserId = share.getTask().getUser().getId();
		List<String> outputs = daoManager.getTasksOutputFilesDAO().getOutputs(share.getTask());
		for(String output : outputs) {
			File child = new File(output);
			if(child.exists()) {
				Boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, output);
				if(!permissionResult)
					throw new PermissionDeniedException();
				else {
					String displayPath = share.getTask().getName() + File.separator + "Output" + File.separator + child.getName();
					boolean filter = false; 
	                FileTypeEnum fileType = FileTypeEnum.PLAIN_TEXT;
	                if(fileFilter != null) {
	                	fileType = getFileType(authenticationToken, child.getAbsolutePath());
	                	filter = filter(fileType, fileFilter);
	                }
	                	 
	                if(!filter) 
	                	if(child.isDirectory())
	                		result.add(createFolderTreeItem(child.getName(), getDisplayName(child), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false, 
	                				FileSource.SHARED));
	                	else
	                		result.add(createFileTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false, 
	                				FileSource.SHARED));					
				}
			}
		}
		return result;
	}

	private List<FileTreeItem> createSharedTaskFolder(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		int shareId = Integer.parseInt(folderTreeItem.getFilePath().replace("Share.", ""));
		Share share = daoManager.getShareDAO().getShare(shareId);
		int shareOwnerUserId = share.getTask().getUser().getId();
		FileTreeItem output = createFolderTreeItem("Output", "Output", "Share.Output." + share.getId(), 
				"Output", FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false, FileSource.SHARED);
		FileTreeItem input = createFolderTreeItem("Input", "Input", "Share.Input." + share.getId(), "Input", 
				FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false, FileSource.SHARED);
		result.add(input);
		result.add(output);
		return result;
	}

	private List<FileTreeItem> createSharedFolders(AuthenticationToken authenticationToken, FileFilter fileFilter) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Share> shares = daoManager.getShareDAO().getSharesOfInvitee(user);
		
		for(Share share : shares) {
			int shareOwnerUserId = share.getTask().getUser().getId();
			result.add(createFolderTreeItem(share.getTask().getName(), share.getTask().getName(),
					"Share." + share.getId(), share.getTask().getName(), FileTypeEnum.DIRECTORY, 
					shareOwnerUserId, false, false, false, FileSource.SHARED));
		}
		return result;
	}

	private List<FileTreeItem> createRootFiles(AuthenticationToken token, FileFilter fileFilter) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		result.add(this.getOwnedRootFolder(token));
		result.add(createFolderTreeItem("Shared", "Shared", "Shared", "Shared", FileTypeEnum.DIRECTORY,
				token.getUserId(), true, false, false, FileSource.SYSTEM));
		result.add(createFolderTreeItem("Public", "Public", Configuration.publicFolder, "Public", FileTypeEnum.DIRECTORY,
				token.getUserId(), true, false, false, FileSource.SYSTEM));
		return result;
	}

	private List<FileTreeItem> createFilesByPath(AuthenticationToken token, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		String filePath = Configuration.fileBase + File.separator + token.getUserId();
		if(folderTreeItem != null)
			filePath = folderTreeItem.getFilePath();
		
		 boolean permissionResult = filePermissionService.hasReadPermission(token, filePath);
	        if(!permissionResult)
	            throw new PermissionDeniedException();
	        else {
	            File file = new File(filePath);
	            File[] childFiles = file.listFiles();
	            Arrays.sort(childFiles);
	            for(File child : childFiles) {
	                String childPath = child.getAbsolutePath(); 
	                permissionResult = filePermissionService.hasReadPermission(token, childPath);
	                if(!permissionResult)
	                    throw new PermissionDeniedException();

	            	String name = child.getName();
	            	String displayName = getDisplayName(child);
					String displayPath = childPath.replace(Configuration.fileBase + File.separator + token.getUserId(), "");
	
					boolean filter = false;
					FileTypeEnum fileType = getFileType(token, child.getAbsolutePath());
					if(fileFilter != null)
						filter = filter(fileType, fileFilter);
	
					FileSource fileSource = getFileSource(token, child.getAbsolutePath());
					if (!filter)
						if (child.isDirectory()) {
							boolean isSystemFile = false;
							boolean isAllowsNewFiles = fileSource.equals(FileSource.OWNED);
							boolean isAllowsNewFolders = fileSource.equals(FileSource.OWNED);
							result.add(createFolderTreeItem(name, displayName,
									child.getAbsolutePath(), displayPath, fileType,
									getOwnerUserId(child), isSystemFile,
									isAllowsNewFiles, isAllowsNewFolders, fileSource));
						} else {
							boolean isSystemFile = false;
							boolean isAllowsNewFiles = false;
							boolean isAllowsNewFolders = fileSource.equals(FileSource.OWNED);
							result.add(createFileTreeItem(name,
									child.getAbsolutePath(), displayPath, fileType,
									getOwnerUserId(child), isSystemFile,
									isAllowsNewFiles, isAllowsNewFolders, fileSource));
						}
	            }
	        }
		
		return result;
	}

	private FileSource getFileSource(AuthenticationToken token, String path) {
		if(path.startsWith(Configuration.publicFolder))
			return FileSource.PUBLIC;
		if(path.startsWith(Configuration.fileBase + File.separator + token.getUserId()))
			return FileSource.OWNED;
		return FileSource.SHARED;
	}

	private String getDisplayName(File child) {
		if(child.isDirectory()) {
        	int directories = child.listFiles(new java.io.FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}
        	}).length;
        	
        	int files = child.listFiles(new java.io.FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isFile();
				}
        	}).length;
        	return child.getName() + " [" + files + " files, " + directories + " directories]";
    	} else {
    		return child.getName();
    	}
	}

	@Override
	public void deleteFiles(AuthenticationToken token, List<FileTreeItem> fileTreeItems) throws PermissionDeniedException, FileDeleteFailedException {
		for(FileTreeItem fileTreeItem : fileTreeItems) {
			this.deleteFile(token, fileTreeItem.getFilePath());
		}
	}
	
	@Override
	public String validateTaxonNames(AuthenticationToken authenticationToken,
			String directory) {
		String result = "success";
		TaxonNameValidator nameValidator = new TaxonNameValidator();
		if(!nameValidator.validate(new File(directory).listFiles())){
			result = nameValidator.getInvalidMessage();
		}
		return result;
	}
	
	@Override
	public List<FileTreeItem> getTaxonomies(AuthenticationToken token, FolderTreeItem folderTreeItem) {
		if(folderTreeItem == null)
			return createRootTaxonomies(token);
		else 
			return createTaxonomiesByPath(token, folderTreeItem);		
	}
	
	private List<FileTreeItem> createRootTaxonomies(AuthenticationToken token) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		result.add(createFolderTreeItem("Owned", "Owned", "Owned", "Owned", FileTypeEnum.DIRECTORY,
				-1, true, false, false, FileSource.SYSTEM));
		result.add(createFolderTreeItem("Shared", "Shared", "Shared", "Shared", FileTypeEnum.DIRECTORY,
				-1, true, false, false, FileSource.SYSTEM));
		result.add(createFolderTreeItem("Public", "Public", "Public", "Public", FileTypeEnum.DIRECTORY,
				-1, true, false, false, FileSource.SYSTEM));
		return result;
	}
	
	private List<FileTreeItem> createTaxonomiesByPath(AuthenticationToken token, FolderTreeItem folderTreeItem) {
		switch(folderTreeItem.getFilePath()) {
		case "Owned":
			return createOwnedTaxonomies(token);
		case "Shared":
			return createSharedTaxnoomies(token);
		case "Public":
			return createPublicTaxonomies(token);
		}
		return new LinkedList<FileTreeItem>();
	}

	private List<FileTreeItem> createPublicTaxonomies(AuthenticationToken token) {
		return createTaxonomies(new File(Configuration.publicFolder), FileSource.PUBLIC);
	}

	private List<FileTreeItem> createSharedTaxnoomies(AuthenticationToken token) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		ShortUser user = daoManager.getUserDAO().getShortUser(token.getUserId());
		List<Share> shares = daoManager.getShareDAO().getSharesOfInvitee(user);
		for(Share share : shares) {
			AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
			List<String> inputFiles = taskConfiguration.getInputs();
			for(String inputFile : inputFiles) 
				result.addAll(createTaxonomies(new File(inputFile), FileSource.SHARED));
			for(String outputFile : taskConfiguration.getOutputs()) {
				result.addAll(createTaxonomies(new File(outputFile), FileSource.SHARED));
			}
		}
		return result;
	}

	private List<FileTreeItem> createOwnedTaxonomies(AuthenticationToken token) {
   		return createTaxonomies(new File(Configuration.fileBase + File.separator + token.getUserId()), FileSource.OWNED);
	}
	
	public List<FileTreeItem> createTaxonomies(File file, FileSource fileSource) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		if(file.isDirectory()) {
			if(isTaxonomyDirectory(file)) {
				String taxonomyRoot = getTaxonomyRoot(getTaxonomyModelFile(file));
				result.add(this.createFileTreeItem(taxonomyRoot, 
						file.getAbsolutePath(), taxonomyRoot, FileTypeEnum.MATRIX_GENERATION_SERIALIZED_MODEL, getOwnerUserId(file), false, false, false, fileSource));
			} else {
				for(File child : file.listFiles()) {
					result.addAll(createTaxonomies(child, fileSource));
				}
			}
		}
		return result;
	}
	
	private int getOwnerUserId(File file) {
		String path = file.getAbsolutePath();
		if(path.startsWith(Configuration.fileBase)) {
			String left = path.replace(Configuration.fileBase + File.separator, "");
			left = left.substring(0, left.indexOf(File.separator));
			return Integer.valueOf(left);
		}
		return -1;
	}

	private String getTaxonomyRoot(File file) {
		MatrixGenerationSerializedModelReader reader = new MatrixGenerationSerializedModelReader();
		Model model = reader.getModel(file.getAbsolutePath());
		Taxon taxon = model.getTaxonMatrix().getHierarchyRootTaxa().get(0);
		return taxon.getTaxonIdentification().getRankData().getLast().displayName();
	}
	
	private boolean isTaxonomyDirectory(File file) {
		return this.getTaxonomyModelFile(file) != null;
	}
	
	private File getTaxonomyModelFile(File file) {
		MatrixGenerationSerializedModelReader reader = new MatrixGenerationSerializedModelReader();
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				if(child.isFile() && reader.isValid(child)) 
					return child;
			}
		}
		return null;
	}

	@Override
	public void copyFile(AuthenticationToken authenticationToken, String sourceFile, String destinationFile) throws CopyFilesFailedException {
		try {
			FileUtils.copyFile(new File(sourceFile), new File(destinationFile));
		} catch(Exception e) {
			String message = "Couldn't copy file";
			log(LogLevel.ERROR, message, e);
			throw new CopyFilesFailedException(message);
		} 
	}
	
	@Override
	public void copyDirectory(AuthenticationToken authenticationToken, String source, String destination) throws CopyFilesFailedException, PermissionDeniedException {
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
	public FileTreeItem getTermReviewFileFromMatrixGenerationOutput(AuthenticationToken token, String matrixGenerationOutput) {
		String[] parts = matrixGenerationOutput.split("_output_by_MG_task_");
		return this.getTermReviewFileFromTextCaptureOutput(token, parts[0]);
	}

	@Override
	public FileTreeItem getTermReviewFileFromTextCaptureOutput(AuthenticationToken token, String textCaptureOutputPath) {
		String reviewTermsPath = textCaptureOutputPath.replaceAll("_output_by_TC_task_", "_TermsReviewed_by_TC_task_");
		File file = new File(reviewTermsPath);
		if(file.exists() && file.isDirectory()) {
			if(file.listFiles().length != 3)
				return null;
			for(File child : file.listFiles()) {
				if(child.isDirectory())
					return null;
				if(!((child.getName().startsWith("category_definition-task-") && child.getName().endsWith(".csv")) || 
						(child.getName().startsWith("category_mainterm_synonymterm-task-") && child.getName().endsWith(".csv")) || 
						(child.getName().startsWith("category_term-task-") && child.getName().endsWith(".csv")))) {
					return null;
				}
			}
			
			String path = file.getAbsolutePath();
			FileSource fileSource = null;
			if(path.startsWith(Configuration.fileBase + File.separator + token.getUserId()))
				fileSource = FileSource.OWNED;
			if(path.startsWith(Configuration.publicFolder))
				fileSource = FileSource.PUBLIC;
			if(path.startsWith(Configuration.fileBase + File.separator) && !path.startsWith(Configuration.fileBase + File.separator + token.getUserId()))
				fileSource = FileSource.SHARED;
			FilePathShortener filePathShortener = new FilePathShortener();
			String displayPath = filePathShortener.shorten(path, fileSource, token.getUserId());
			return createFolderTreeItem(file.getName(), getDisplayName(file), file.getAbsolutePath(), displayPath, null, getOwnerUserId(file), false, 
					false, false, fileSource);
		}
		return null;
	}

	//returns the first possiblity it can find
	@Override
	public FileTreeItem getOntologyInputFileFromTextCaptureOutput(AuthenticationToken token, String textCaptureOutput) {
		File file = new File(textCaptureOutput);
		String prefix = file.getName() + "_output_by_OB_task_";
		String ontologyPath = null;
		if(file.exists() && file.isDirectory()) {
			for(File sibling : file.getParentFile().listFiles()) {
				if(sibling.getName().startsWith(prefix)) {
					ontologyPath = sibling.getAbsolutePath();
					break;
				}
			}
		}
		
		File ontologyFile = new File(ontologyPath);
		if(ontologyFile.exists() && ontologyFile.isDirectory()) {
			for(File child : ontologyFile.listFiles()) {
				if(child.isDirectory())
					return null;
			}
			
			String path = ontologyFile.getAbsolutePath();
			FileSource fileSource = null;
			if(path.startsWith(Configuration.fileBase + File.separator + token.getUserId()))
				fileSource = FileSource.OWNED;
			if(path.startsWith(Configuration.publicFolder))
				fileSource = FileSource.PUBLIC;
			if(path.startsWith(Configuration.fileBase + File.separator) && !path.startsWith(Configuration.fileBase + File.separator + token.getUserId()))
				fileSource = FileSource.SHARED;
			FilePathShortener filePathShortener = new FilePathShortener();
			String displayPath = filePathShortener.shorten(path, fileSource, token.getUserId());
			return createFolderTreeItem(ontologyFile.getName(), getDisplayName(ontologyFile), 
					ontologyFile.getAbsolutePath(), displayPath, null, getOwnerUserId(ontologyFile), false, 
					false, false, fileSource);
		}
		return null;
	}
	
	@Override
	public FileTreeItem getOntologyInputFileFromMatrixGenerationOutput(
			AuthenticationToken token, String matrixGenerationOutput) {		
		String[] parts = matrixGenerationOutput.split("_output_by_MG_task_");
		String textCaptureOutput = parts[0];
		return this.getOntologyInputFileFromTextCaptureOutput(token, textCaptureOutput);
	}
}


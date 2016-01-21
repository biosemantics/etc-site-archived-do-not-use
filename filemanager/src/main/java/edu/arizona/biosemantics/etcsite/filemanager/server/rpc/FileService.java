package edu.arizona.biosemantics.etcsite.filemanager.server.rpc;

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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.validation.key.KeyElementValidator;
import edu.arizona.biosemantics.common.validation.key.KeyValidationException;
import edu.arizona.biosemantics.etcsite.core.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.core.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.core.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.core.shared.model.Share;
import edu.arizona.biosemantics.etcsite.core.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.server.Configuration;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.MatrixGenerationSerializedModelReader;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.validate.CleanTaxReader;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.validate.TaxonNameValidator;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.process.FileNameNormalizer;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.CreateFileFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.FileDeleteFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.MoveFileFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.RenameFileFailedException;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.ZipDirectoryFailedException;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class FileService extends RemoteServiceServlet implements IFileService {

	private static final long serialVersionUID = -9193602268703418530L;
	private IFilePermissionService filePermissionService;
	private IFileFormatService fileFormatService;
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy");
	private	XmlNamespaceManager xmlNamespaceManager;
	private FileNameNormalizer fileNameNormalizer;
	private DAOManager daoManager;
	
	@Inject
	public FileService(IFileFormatService fileFormatService, IFilePermissionService filePermissionService, 
			DAOManager daoManager, FileNameNormalizer fileNameNormalizer, XmlNamespaceManager xmlNamespaceManager) {
		this.fileFormatService = fileFormatService;
		this.filePermissionService = filePermissionService;
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
	public List<FileTreeItem> getFiles(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException {		
		if(folderTreeItem == null)
			return createRootFiles(authenticationToken, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith(Configuration.publicFolder))
			return createFilesByPath(authenticationToken, folderTreeItem, fileFilter, true, false, false);
		else if(folderTreeItem.getFilePath().equals("Shared"))
			return createSharedFolders(authenticationToken, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.Output."))
			return createSharedOutputTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.Input.")) 
			return createSharedInputTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else if(folderTreeItem.getFilePath().startsWith("Share.")) 
			return createSharedTaskFolder(authenticationToken, folderTreeItem, fileFilter);
		else 
			return createFilesByPath(authenticationToken, folderTreeItem, fileFilter, false, true, true);
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
	                		result.add(createFolderTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false));
	                	else
	                		result.add(createFileTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false));						
				}
			}
		}
		return result;
	}

	private FileTreeItem createFileTreeItem(String name, String path, String displayPath, FileTypeEnum fileType, int shareOwnerUserId, boolean isSystemFile, 
			boolean isAllowsNewFiles, boolean isAllowsNewFolders) {
		return new FileTreeItem(UUID.randomUUID().toString(), name, path, displayPath, fileType, shareOwnerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders); 
	}
	
	private FolderTreeItem createFolderTreeItem(String name, String path, String displayPath, FileTypeEnum fileType, int shareOwnerUserId, boolean isSystemFile, 
			boolean isAllowsNewFiles, boolean isAllowsNewFolders) {
		return new FolderTreeItem(UUID.randomUUID().toString(), name, path, displayPath, fileType, shareOwnerUserId, isSystemFile, isAllowsNewFiles, isAllowsNewFolders); 
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
	                		result.add(createFolderTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false));
	                	else
	                		result.add(createFileTreeItem(child.getName(), child.getAbsolutePath(), displayPath, fileType, shareOwnerUserId, false, false, false));					
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
		FileTreeItem output = createFolderTreeItem("Output", "Share.Output." + share.getId(), 
				"Output", FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false);
		FileTreeItem input = createFolderTreeItem("Input", "Share.Input." + share.getId(), "Input", 
				FileTypeEnum.DIRECTORY, shareOwnerUserId, false, false, false);
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
			result.add(createFolderTreeItem(share.getTask().getName(), "Share." + share.getId(), share.getTask().getName(), FileTypeEnum.DIRECTORY, 
					shareOwnerUserId, false, false, false));
		}
		return result;
	}

	private List<FileTreeItem> createRootFiles(AuthenticationToken authenticationToken, FileFilter fileFilter) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		result.add(createFolderTreeItem("Owned", edu.arizona.biosemantics.etcsite.core.server.Configuration.fileBase + File.separator + authenticationToken.getUserId(), "Owned", FileTypeEnum.DIRECTORY,
				 authenticationToken.getUserId(), true, false, true));
		result.add(createFolderTreeItem("Shared", "Shared", "Shared", FileTypeEnum.DIRECTORY,
				 authenticationToken.getUserId(), true, false, false));
		result.add(createFolderTreeItem("Public", Configuration.publicFolder, "Public", FileTypeEnum.DIRECTORY,
				 authenticationToken.getUserId(), true, false, false));
		return result;
	}

	private List<FileTreeItem> createFilesByPath(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter, 
			boolean isSystemFile, boolean allowNewFolders, boolean allowNewFiles) throws PermissionDeniedException {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		String filePath = edu.arizona.biosemantics.etcsite.core.server.Configuration.fileBase + File.separator + authenticationToken.getUserId();
		if(folderTreeItem != null)
			filePath = folderTreeItem.getFilePath();
		
		 boolean permissionResult = filePermissionService.hasReadPermission(authenticationToken, filePath);
	        if(!permissionResult)
	            throw new PermissionDeniedException();
	        else {
	            File file = new File(filePath);
	            File[] childFiles = file.listFiles();
	            Arrays.sort(childFiles);
	            for(File child : childFiles) {
	                String childPath = child.getAbsolutePath(); 
	                permissionResult = filePermissionService.hasReadPermission(authenticationToken, childPath);
	                if(!permissionResult)
	                    throw new PermissionDeniedException();

	            	String name = child.getName();
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
		            	name = child.getName() + " [" + files + " files, " + directories + " directories]";
	            	}
				String displayPath = childPath.replace(edu.arizona.biosemantics.etcsite.core.server.Configuration.fileBase + File.separator + authenticationToken.getUserId(), "");

				boolean filter = false;
				FileTypeEnum fileType = FileTypeEnum.PLAIN_TEXT;
				if (fileFilter != null) {
					fileType = getFileType(authenticationToken,
							child.getAbsolutePath());
					filter = filter(fileType, fileFilter);
				}

				if (!filter)
					if (child.isDirectory())
						result.add(createFolderTreeItem(name,
								child.getAbsolutePath(), displayPath, fileType,
								authenticationToken.getUserId(), isSystemFile,
								(allowNewFolders ? child.isDirectory() : false), (allowNewFiles ? true : false)));
					else
						result.add(createFileTreeItem(name,
								child.getAbsolutePath(), displayPath, fileType,
								authenticationToken.getUserId(), isSystemFile,
								(allowNewFolders ? child.isDirectory() : false), false));
	            }
	        }
		
		return result;
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
		return createTaxonomies(new File(Configuration.publicFolder));
	}

	private List<FileTreeItem> createSharedTaxnoomies(AuthenticationToken token) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		ShortUser user = daoManager.getUserDAO().getShortUser(token.getUserId());
		List<Share> shares = daoManager.getShareDAO().getSharesOfInvitee(user);
		for(Share share : shares) {
			AbstractTaskConfiguration taskConfiguration = share.getTask().getConfiguration();
			List<String> inputFiles = taskConfiguration.getInputs();
			for(String inputFile : inputFiles) 
				result.addAll(createTaxonomies(new File(inputFile)));
			for(String outputFile : taskConfiguration.getOutputs()) {
				result.addAll(createTaxonomies(new File(outputFile)));
			}
		}
		return result;
	}

	private List<FileTreeItem> createOwnedTaxonomies(AuthenticationToken token) {
   		return createTaxonomies(new File(edu.arizona.biosemantics.etcsite.core.server.Configuration.fileBase + File.separator + token.getUserId()));
	}
	
	public List<FileTreeItem> createTaxonomies(File file) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		if(file.isDirectory()) {
			if(isTaxonomyDirectory(file)) {
				String taxonomyRoot = getTaxonomyRoot(getTaxonomyModelFile(file));
				result.add(this.createFileTreeItem(taxonomyRoot, 
						file.getAbsolutePath(), taxonomyRoot, FileTypeEnum.MATRIX_GENERATION_SERIALIZED_MODEL, -1, false, false, false));
			} else {
				for(File child : file.listFiles()) {
					result.addAll(createTaxonomies(child));
				}
			}
		}
		return result;
	}

	private File getTaxonomyModelFile(File file) {
		System.out.println(file.getAbsolutePath());
		MatrixGenerationSerializedModelReader reader = new MatrixGenerationSerializedModelReader();
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				if(child.isFile() && reader.isValid(child)) 
					return child;
			}
		}
		return null;
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

	private List<FileTreeItem> createRootTaxonomies(AuthenticationToken token) {
		List<FileTreeItem> result = new LinkedList<FileTreeItem>();
		result.add(createFolderTreeItem("Owned", "Owned", "Owned", FileTypeEnum.DIRECTORY,
				-1, true, false, false));
		result.add(createFolderTreeItem("Shared", "Shared", "Shared", FileTypeEnum.DIRECTORY,
				-1, true, false, false));
		result.add(createFolderTreeItem("Public", "Public", "Public", FileTypeEnum.DIRECTORY,
				-1, true, false, false));
		return result;
	}


}


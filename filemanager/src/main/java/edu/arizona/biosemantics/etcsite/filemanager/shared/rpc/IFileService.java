package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;

@RemoteServiceRelativePath("file")
public interface IFileService extends RemoteService {
	
	public void deleteFile(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, 
		FileDeleteFailedException;

	public void moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath) throws MoveFileFailedException, 
		PermissionDeniedException;

	public String createDirectory(AuthenticationToken authenticationToken, String filePath, String idealFolderName, boolean force)
		throws PermissionDeniedException, CreateDirectoryFailedException;

	public boolean isDirectory(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;
	
	public boolean isFile(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;

	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath)throws PermissionDeniedException;

	public String createFile(AuthenticationToken authenticationToken, String directory, String idealFileName, boolean force)
			throws CreateFileFailedException, PermissionDeniedException;
	
	public Integer getDepth(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;
	
	public String zipDirectory(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, 
			ZipDirectoryFailedException;

	public void setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task)
			throws PermissionDeniedException;
	
	public boolean isInUse(AuthenticationToken authenticationToken, String filePath);
	
	public List<Task> getUsingTasks(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;	
	
	public void renameFile(AuthenticationToken authenticationToken, String path, String newFileName) throws PermissionDeniedException, 
			RenameFileFailedException;

	public String getParent(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;

	public String getFileName(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException;

	public void copyDirectory(AuthenticationToken authenticationToken, String source, String destination) throws 
		CopyFilesFailedException, PermissionDeniedException;
	
	public String getDownloadPath(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, 
		ZipDirectoryFailedException;
	
	public HashMap<String,String> validateKeys(AuthenticationToken authenticationToken, String directory, List<String> uploadedFiles);
	
	public String validateTaxonNames(AuthenticationToken authenticationToken, String directory);

	public void deleteUploadedFiles(AuthenticationToken token, String uploadedDirectory, List<String> uploadedFiles) throws PermissionDeniedException, FileDeleteFailedException;
	
	public List<FileTreeItem> getFiles(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException;

	public void deleteFiles(AuthenticationToken token, List<FileTreeItem> selection) throws PermissionDeniedException, FileDeleteFailedException;
	
	public List<FileTreeItem> getTaxonomies(AuthenticationToken token, FolderTreeItem loadConfig);

	public void copyFile(AuthenticationToken authenticationToken, String sourceFile, String destinationFile)  throws CopyFilesFailedException;
	
}

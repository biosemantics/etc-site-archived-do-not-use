package edu.arizona.biosemantics.etcsite.shared.rpc.file;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.Tree;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;

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

	public void copyFiles(AuthenticationToken authenticationToken, String source, String destination) throws 
		CopyFilesFailedException, PermissionDeniedException;
	
	public String getDownloadPath(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, 
		ZipDirectoryFailedException;
	
	public HashMap<String,String> validateKeys(AuthenticationToken authenticationToken, String directory, List<String> uploadedFiles);
	
	public String validateTaxonNames(AuthenticationToken authenticationToken, String directory);

	public void deleteUploadedFiles(AuthenticationToken token, String uploadedDirectory, List<String> uploadedFiles) throws PermissionDeniedException, FileDeleteFailedException;
	
	public FolderTreeItem getOwnedRootFolder(AuthenticationToken authenticationToken);
	
	public List<FileTreeItem> getFiles(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter) throws PermissionDeniedException;

	public void deleteFiles(AuthenticationToken token, List<FileTreeItem> selection) throws PermissionDeniedException, FileDeleteFailedException;
	
	public List<FileTreeItem> getTaxonomies(AuthenticationToken token, FolderTreeItem loadConfig);
	
	public void copyFile(AuthenticationToken authenticationToken, String sourceFile, String destinationFile)  throws CopyFilesFailedException;

	public void copyDirectory(AuthenticationToken authenticationToken, String source, String destination) throws CopyFilesFailedException,
			PermissionDeniedException;

	public FileTreeItem getTermReviewFileFromTextCaptureOutput(AuthenticationToken token, String matrixReviewModelPath);

	public FileTreeItem getOntologyInputFileFromTextCaptureOutput(AuthenticationToken token,
			String fullPath);

	public FileTreeItem getTermReviewFileFromMatrixGenerationOutput(
			AuthenticationToken token, String matrixGenerationOutput);
	
	public FileTreeItem getOntologyInputFileFromMatrixGenerationOutput(
			AuthenticationToken token, String modelPath);
}

package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;

public interface IFileServiceAsync {

	public void deleteFile(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Void> callback);
	
	public void moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath, AsyncCallback<Void> callback);

	public void createDirectory(AuthenticationToken authenticationToken, String filePath, String idealFolderName, boolean force, AsyncCallback<String> callback);
	
	public void isDirectory(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void isFile(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);

	public void getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath, AsyncCallback<List<String>> callback);

	public void createFile(AuthenticationToken authenticationToken, String directory, String idealFileName, boolean force, AsyncCallback<String> callback);

	public void getDepth(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Integer> callback);

	public void zipDirectory(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);
	
	public void setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task, AsyncCallback<Void> callback);
	
	public void isInUse(AuthenticationToken authenticationToken, String filePath, AsyncCallback<Boolean> callback);
	
	public void getUsingTasks(AuthenticationToken authenticationToken, String filePath, AsyncCallback<List<Task>> callback);

	public void renameFile(AuthenticationToken authenticationToken, String path, String newFileName, AsyncCallback<Void> callback);
	
	public void getParent(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	public void getFileName(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	public void copyDirectory(AuthenticationToken authenticationToken, String source, String destination, AsyncCallback<Void> callback);
	
	public void getDownloadPath(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	void validateKeys(AuthenticationToken authenticationToken,
			String directory, List<String> uploadedFiles,
			AsyncCallback<HashMap<String, String>> callback);

	void deleteUploadedFiles(AuthenticationToken token,
			String uploadedDirectory, List<String> uploadedFiles,
			AsyncCallback<Void> asyncCallback);

	public void getFiles(AuthenticationToken authenticationToken, FolderTreeItem folderTreeItem, FileFilter fileFilter, AsyncCallback<List<FileTreeItem>> callback);

	public void deleteFiles(AuthenticationToken token, List<FileTreeItem> selection, AsyncCallback<Void> asyncCallback);

	void validateTaxonNames(AuthenticationToken authenticationToken,
			String directory, AsyncCallback<String> callback);

	public void getTaxonomies(AuthenticationToken token, FolderTreeItem loadConfig, AsyncCallback<List<FileTreeItem>> callback);
	
	public void copyFile(AuthenticationToken authenticationToken, String sourceFile, String destinationFile, AsyncCallback<Void> callback);

}

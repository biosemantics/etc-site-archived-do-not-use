package edu.arizona.biosemantics.etcsite.shared.rpc.file;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.Tree;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IFileServiceAsync {

	public void getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter, AsyncCallback<Tree<FileInfo>> callback);

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

	public void copyFiles(AuthenticationToken authenticationToken, String source, String destination, AsyncCallback<Void> callback);
	
	public void getDownloadPath(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);
}
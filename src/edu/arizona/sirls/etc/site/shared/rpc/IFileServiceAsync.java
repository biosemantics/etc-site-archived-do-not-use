package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

public interface IFileServiceAsync {

	public void getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter, AsyncCallback<RPCResult<Tree<FileInfo>>> callback);

	public void deleteFile(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Void>> callback);
	
	public void moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath, AsyncCallback<RPCResult<Void>> callback);

	public void createDirectory(AuthenticationToken authenticationToken, String filePath, String name, AsyncCallback<RPCResult<Void>> callback);
	
	public void isDirectory(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isFile(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);

	public void getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<List<String>>> callback);

	public void createFile(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Void>> callback);

	public void getDepth(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Integer>> asyncCallback);

	public void zipDirectory(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Void>> callback);
	
	public void setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task, AsyncCallback<RPCResult<Void>> callback);
	
	public void isInUse(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void getUsingTasks(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<List<Task>>> callback);
}

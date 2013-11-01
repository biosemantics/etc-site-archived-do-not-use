package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

@RemoteServiceRelativePath("file")
public interface IFileService extends RemoteService {

	public RPCResult<Tree<FileInfo>> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter); 
	
	public RPCResult<Void> deleteFile(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Void> moveFile(AuthenticationToken authenticationToken, String filePath, String newFilePath);

	public RPCResult<Void> createDirectory(AuthenticationToken authenticationToken, String filePath, String name);

	public RPCResult<Boolean> isDirectory(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isFile(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<List<String>> getDirectoriesFiles(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Void> createFile(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Integer> getDepth(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Void> zipDirectory(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Void> setInUse(AuthenticationToken authenticationToken, boolean value, String filePath, Task task);
	
	public RPCResult<Boolean> isInUse(AuthenticationToken authenticationToken, String filePath);	
	
	public RPCResult<List<Task>> getUsingTasks(AuthenticationToken authenticationToken, String filePath);
	
}

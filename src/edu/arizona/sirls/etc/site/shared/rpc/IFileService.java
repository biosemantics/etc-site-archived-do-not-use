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

	public Tree<FileInfo> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter); 
	
	public MessageResult deleteFile(AuthenticationToken authenticationToken, String target);

	public MessageResult moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget);

	public MessageResult createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName);

	public boolean isDirectory(AuthenticationToken authenticationToken, String target);
	
	public boolean isFile(AuthenticationToken authenticationToken, String target);

	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String inputDirectory);

	public MessageResult createFile(AuthenticationToken authenticationToken, String outputFile);
	
	public Integer getDepth(AuthenticationToken authenticationToken, String sourcePath);
	
	public void zipDirectory(AuthenticationToken authenticationToken, String target);

	public void setInUse(AuthenticationToken authenticationToken, boolean value, String input, Task task);
	
	public boolean isInUse(AuthenticationToken authenticationToken, String input);	
	
	public List<Task> getUsingTasks(AuthenticationToken authenticationToken, String input);
	
}

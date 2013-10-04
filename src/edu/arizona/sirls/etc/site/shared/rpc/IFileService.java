package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

@RemoteServiceRelativePath("file")
public interface IFileService extends RemoteService {

	public Tree<String> getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter); 
	
	public boolean deleteFile(AuthenticationToken authenticationToken, String target);

	public boolean moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget);

	public boolean createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName);

	public boolean isDirectory(AuthenticationToken authenticationToken, String target);
	
	public boolean isFile(AuthenticationToken authenticationToken, String target);

	public List<String> getDirectoriesFiles(AuthenticationToken authenticationToken, String inputDirectory);

	public boolean createFile(AuthenticationToken authenticationToken, String outputFile);
	
	public Integer getDepth(AuthenticationToken authenticationToken, String sourcePath);
	
	public void zipDirectory(AuthenticationToken authenticationToken, String target);
	
}

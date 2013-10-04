package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public interface IFileServiceAsync {

	public void getUsersFiles(AuthenticationToken authenticationToken, FileFilter fileFilter, AsyncCallback<Tree<String>> callback);

	public void deleteFile(AuthenticationToken authenticationToken, String target,
			AsyncCallback<Boolean> callback);
	
	public void moveFile(AuthenticationToken authenticationToken, String target,
			String newTarget, AsyncCallback<Boolean> callback);

	public void createDirectory(AuthenticationToken authenticationToken, String target,
			String directoryName, AsyncCallback<Boolean> callback);
	
	public void isDirectory(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);
	
	public void isFile(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);

	public void getDirectoriesFiles(AuthenticationToken authenticationToken, String inputDirectory, AsyncCallback<List<String>> callback);

	public void createFile(AuthenticationToken authenticationToken, String outputFile, AsyncCallback<Boolean> callback);

	public void getDepth(AuthenticationToken authenticationToken, String sourcePath, AsyncCallback<Integer> asyncCallback);

	public void zipDirectory(AuthenticationToken authenticationToken, String target, AsyncCallback<Void> callback);
}

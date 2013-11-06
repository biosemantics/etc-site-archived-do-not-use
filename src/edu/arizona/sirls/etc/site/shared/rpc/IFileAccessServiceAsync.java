package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public interface IFileAccessServiceAsync {
	
	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content, AsyncCallback<RPCResult<Void>> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<String>> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, FileType fileType, AsyncCallback<RPCResult<String>> callback);

	public void getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileType taxonDescription, AsyncCallback<RPCResult<String>> asyncCallback);

	
}

package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;

public interface IFileAccessServiceAsync {
	
	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content, AsyncCallback<RPCResult<Void>> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<String>> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType, AsyncCallback<RPCResult<String>> callback);

	public void getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum taxonDescription, AsyncCallback<RPCResult<String>> asyncCallback);

	
}

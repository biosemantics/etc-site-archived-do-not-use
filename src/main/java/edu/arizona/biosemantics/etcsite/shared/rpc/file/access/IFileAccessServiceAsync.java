package edu.arizona.biosemantics.etcsite.shared.rpc.file.access;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IFileAccessServiceAsync {
	
	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content, AsyncCallback<Void> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType, AsyncCallback<String> callback);

	public void getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum taxonDescription, AsyncCallback<String> callback);

	
}

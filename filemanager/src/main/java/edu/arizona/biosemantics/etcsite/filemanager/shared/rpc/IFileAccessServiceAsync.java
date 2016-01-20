package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.common.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.common.shared.rpc.auth.AuthenticationToken;


public interface IFileAccessServiceAsync {
	
	public void setFileContent(AuthenticationToken authenticationToken, String filePath, String content, AsyncCallback<Void> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);

	public void getFileContent(AuthenticationToken authenticationToken, String filePath, FileTypeEnum fileType, AsyncCallback<String> callback);

	public void getFileContentHighlighted(AuthenticationToken authenticationToken, String filePath, FileTypeEnum taxonDescription, AsyncCallback<String> callback);

	
}

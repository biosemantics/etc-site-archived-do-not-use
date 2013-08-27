package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.file.DeleteFileAsyncCallback;
import edu.arizona.sirls.etc.site.client.api.file.IDeleteFileAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.IUserFilesAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.UserFilesAsyncCallback;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.client.widget.FileTreeDecorator;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class DeleteClickHandler implements ClickHandler, IDeleteFileAsyncCallbackListener, IUserFilesAsyncCallbackListener {

	private FileSelectionHandler fileSelectionHandler;
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private FileTreeComposite tree;

	public DeleteClickHandler(FileSelectionHandler fileSelectionHandler, FileTreeComposite tree) { 
		this.fileSelectionHandler = fileSelectionHandler;
		this.tree = tree;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		String target = fileSelectionHandler.getTarget();
		//don't allow deletetion of root node
		if(!target.isEmpty()) {
			DeleteFileAsyncCallback callback = new DeleteFileAsyncCallback();
			callback.addListener(this);
			fileService.deleteFile(Authentication.getInstance().getAuthenticationToken(), target, callback);
		}
	}

	@Override
	public void notifyException(Throwable caught) {
		caught.printStackTrace();
	}

	@Override
	public void notifyResult(Boolean result) {
		if(result) {
			UserFilesAsyncCallback callback = new UserFilesAsyncCallback();
			callback.addListener(this);
			fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), callback);
		}	
	}


	@Override
	public void notifyResult(
			edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		tree.refresh();
	}

	@Override
	public void notifyException(Throwable caught,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		caught.printStackTrace();
	}

}

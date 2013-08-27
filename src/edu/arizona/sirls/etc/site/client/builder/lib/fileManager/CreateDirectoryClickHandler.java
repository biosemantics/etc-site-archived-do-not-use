package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.file.CreateDirectoryAsyncCallback;
import edu.arizona.sirls.etc.site.client.api.file.DeleteFileAsyncCallback;
import edu.arizona.sirls.etc.site.client.api.file.ICreateDirectoryAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.IUserFilesAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.UserFilesAsyncCallback;
import edu.arizona.sirls.etc.site.client.builder.dialog.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.builder.dialog.LabelTextFieldDialogBox;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoginDialogBox;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.client.widget.FileTreeDecorator;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class CreateDirectoryClickHandler implements ClickHandler, ICreateDirectoryAsyncCallbackListener, IUserFilesAsyncCallbackListener, ILabelTextFieldDialogBoxHandler {

	private FileSelectionHandler fileSelectionHandler;
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private FileTreeComposite tree;

	public CreateDirectoryClickHandler(FileSelectionHandler fileSelectionHandler, FileTreeComposite tree) { 
		this.fileSelectionHandler = fileSelectionHandler;
		this.tree = tree;
	}
	
	@Override
	public void onClick(ClickEvent event) {		
		LabelTextFieldDialogBox renameDialogBox = new LabelTextFieldDialogBox("Create folder", "Folder name:", "", this);
		renameDialogBox.center();
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
	public void canceled() {
		
	}

	@Override
	public void confirmed(String directoryName) {
		String target = fileSelectionHandler.getTarget();
		CreateDirectoryAsyncCallback callback = new CreateDirectoryAsyncCallback();
		callback.addListener(this);
		fileService.createDirectory(Authentication.getInstance().getAuthenticationToken(), target, directoryName, callback);
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

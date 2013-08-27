package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.file.IMoveFileAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.IUserFilesAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.MoveFileAsyncCallback;
import edu.arizona.sirls.etc.site.client.api.file.UserFilesAsyncCallback;
import edu.arizona.sirls.etc.site.client.builder.dialog.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.builder.dialog.LabelTextFieldDialogBox;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.client.widget.FileTreeDecorator;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class RenameClickHandler implements ClickHandler, IMoveFileAsyncCallbackListener, IUserFilesAsyncCallbackListener, ILabelTextFieldDialogBoxHandler {

	private FileSelectionHandler fileSelectionHandler;
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private FileTreeComposite tree;

	public RenameClickHandler(FileSelectionHandler fileSelectionHandler, FileTreeComposite tree) { 
		this.fileSelectionHandler = fileSelectionHandler;
		this.tree = tree;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		String target = fileSelectionHandler.getTarget();
		//don't allow rename of root node
		if(!target.isEmpty()) {
			String pathParts[] = target.split("//");
			LabelTextFieldDialogBox renameDialogBox = new LabelTextFieldDialogBox("Rename", "New name:", pathParts[pathParts.length-1], this);
			renameDialogBox.center();
		}
	}

	private String getTargetFromParts(String[] parts) {
		StringBuilder builder = new StringBuilder();
		for(String part : parts) {
		    if(builder.length() != 0)
		    	builder.append("//");
			builder.append(part);
		}
		return builder.toString();
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
	public void confirmed(String newFileName) {
		String target = fileSelectionHandler.getTarget();
		MoveFileAsyncCallback callback = new MoveFileAsyncCallback();
		callback.addListener(this);
		String pathParts[] = target.split("//");
		pathParts[pathParts.length-1] = newFileName;
		String newTarget = getTargetFromParts(pathParts);
		fileService.moveFile(Authentication.getInstance().getAuthenticationToken(), target, newTarget, callback);
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

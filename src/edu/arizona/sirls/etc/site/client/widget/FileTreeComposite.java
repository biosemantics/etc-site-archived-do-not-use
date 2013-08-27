package edu.arizona.sirls.etc.site.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.api.file.IUserFilesAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.UserFilesAsyncCallback;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileDragDropHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileSelectionHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.IFileMoveListener;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class FileTreeComposite extends Composite implements IUserFilesAsyncCallbackListener, IFileMoveListener {
	
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private final Tree tree = new Tree();
	private FileSelectionHandler fileSelectionHandler = new FileSelectionHandler();
	private FileDragDropHandler fileDragDropHandler = new FileDragDropHandler();
	private boolean enableDragAndDrop;
	
	public FileTreeComposite(boolean enableDragAndDrop) { 
		this.enableDragAndDrop = enableDragAndDrop;
		tree.addSelectionHandler(fileSelectionHandler);
		initWidget(tree);
	}
	
	public void refresh() {
		UserFilesAsyncCallback userFilesAsyncCallback = new UserFilesAsyncCallback();
		userFilesAsyncCallback.addListener(this);
		this.fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), userFilesAsyncCallback);	
	}

	@Override
	public void notifyResult(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		tree.clear();
		
		FileTreeDecorator fileTreeDecorator = new FileTreeDecorator();
		fileDragDropHandler.addListener(this);
		if(enableDragAndDrop) 
			fileTreeDecorator.decorate(tree, result, fileDragDropHandler);
		else
			fileTreeDecorator.decorate(tree, result, null);
	}

	@Override
	public void notifyException(Throwable caught,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		caught.printStackTrace();
	}

	@Override
	public void notifyFileMove() {
		this.refresh();
	}

	public FileSelectionHandler getFileSelectionHandler() {
		return this.fileSelectionHandler;
	}

}

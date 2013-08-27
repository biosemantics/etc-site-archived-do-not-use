package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class DeleteClickHandler implements ClickHandler {

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
			fileService.deleteFile(Authentication.getInstance().getAuthenticationToken(), target, deleteFileCallback);
		}
	}

	
	private AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> usersFilesCallback = new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {

		public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
			tree.refresh();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}

	};

	private AsyncCallback<Boolean> deleteFileCallback = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result) {
			if (result) {
				fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), usersFilesCallback);
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

}

package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.dialog.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.builder.dialog.LabelTextFieldDialogBox;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class CreateDirectoryClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {

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

	private AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> usersFilesCallback = new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {

		public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
			tree.refresh();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}

	};

	private AsyncCallback<Boolean> createDirectoryCallback = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result) {
			if (result) {
				fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), FileFilter.ALL, usersFilesCallback);
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

	@Override
	public void canceled() {

	}

	@Override
	public void confirmed(String directoryName) {
		String target = fileSelectionHandler.getTarget();
		fileService.createDirectory(Authentication.getInstance().getAuthenticationToken(), target, directoryName, createDirectoryCallback);
	}

}

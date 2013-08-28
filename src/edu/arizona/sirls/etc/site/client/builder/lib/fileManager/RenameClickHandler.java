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

public class RenameClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {

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
	public void canceled() {
		
	}

	@Override
	public void confirmed(String newFileName) {
		String target = fileSelectionHandler.getTarget();
		String pathParts[] = target.split("//");
		pathParts[pathParts.length-1] = newFileName;
		String newTarget = getTargetFromParts(pathParts);
		fileService.moveFile(Authentication.getInstance().getAuthenticationToken(), target, newTarget, moveFileCallback);
	}

	
	private AsyncCallback<Boolean> moveFileCallback = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result) {
			if(result) {
				fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), FileFilter.ALL, userFilesCallback);
			}		
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
	
	private AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> userFilesCallback = new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {
		public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
			tree.refresh();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};


}

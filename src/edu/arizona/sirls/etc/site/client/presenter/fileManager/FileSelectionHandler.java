package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;

public class FileSelectionHandler implements SelectionHandler<TreeItem> {

	private String target = null;
	private boolean targetIsDirectory = false;

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		FileImageLabelTreeItem selection = (FileImageLabelTreeItem)event.getSelectedItem();
		this.target = selection.getPath();
		targetIsDirectory = selection instanceof DirectoryTreeItem;
	}

	public String getTarget() {
		return target;
	}
	
	public boolean isTargetDirectory() {
		return targetIsDirectory;
	}

	public void clear() {
		this.target = null;
		this.targetIsDirectory = false;
	}

	public void setTarget(String newTarget) {
		this.target = newTarget;
	}
		
}

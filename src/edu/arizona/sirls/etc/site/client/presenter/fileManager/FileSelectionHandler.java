package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;

public class FileSelectionHandler implements SelectionHandler<TreeItem> {

	private FileImageLabelTreeItem selection;
	private String selectionPath;

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		if(event.getSelectedItem() instanceof FileImageLabelTreeItem)
			this.selection = (FileImageLabelTreeItem)event.getSelectedItem();
		this.selectionPath = null;
	}

	public FileImageLabelTreeItem getSelection() {
		return selection;
	}
	
	public void setSelectionPath(String filePath) {
		selectionPath = filePath;
	}
	
	public String getSelectionPath() {
		if(selectionPath != null)
			return this.selectionPath;
		if(selection != null)
			return this.selection.getFileInfo().getFilePath();
		return null;
	}
	
	public boolean hasSelectionPath() {
		return this.selectionPath != null || this.selection != null;
	}
	
	public boolean hasSelection() {
		return this.selection != null;
	}


	public void clear() {
		this.selection = null;
	}
		
}

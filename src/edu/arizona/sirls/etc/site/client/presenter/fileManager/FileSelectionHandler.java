package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;

public class FileSelectionHandler implements SelectionHandler<TreeItem> {

	private FileImageLabelTreeItem selection;

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		if(event.getSelectedItem() instanceof FileImageLabelTreeItem)
			this.selection = (FileImageLabelTreeItem)event.getSelectedItem();
	}

	public FileImageLabelTreeItem getSelection() {
		return selection;
	}

	public void clear() {
		this.selection = null;
	}
		
}

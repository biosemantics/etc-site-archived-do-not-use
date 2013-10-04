package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class FileImageLabelTree extends Tree {

	@Override
	public FileImageLabelTreeItem getItem(int index) {
		TreeItem item = super.getItem(index);
		if(item instanceof FileImageLabelTreeItem)
			return (FileImageLabelTreeItem)item;
		return null;
	}
	
}

package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

public class FileImageLabelTreeItem extends TreeItem {

	private FileImageLabelComposite fileImageLabelComposite;
	private FileInfo fileInfo;
	
	public FileImageLabelTreeItem(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	public FileImageLabelTreeItem(FileImageLabelComposite fileImageLabelComposite) {
		this.fileImageLabelComposite = fileImageLabelComposite;
		this.setWidget(fileImageLabelComposite);
	}
	

	public void setFileImageLabelComposite(FileImageLabelComposite fileImageLabelComposite) {
		this.fileImageLabelComposite = fileImageLabelComposite;
		this.setWidget(fileImageLabelComposite);
	}

	public FileImageLabelComposite getFileImageLabelComposite() {
		return fileImageLabelComposite;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	@Override 
	public FileImageLabelTreeItem getChild(int index) {
		TreeItem item = super.getChild(index);
		if(item instanceof FileImageLabelTreeItem)
			return (FileImageLabelTreeItem)item;
		return null;
	}
	
	
	
}

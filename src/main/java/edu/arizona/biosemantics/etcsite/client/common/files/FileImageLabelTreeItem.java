package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.biosemantics.etcsite.shared.file.FileInfo;

public class FileImageLabelTreeItem extends TreeItem {

	private FileImageLabel fileImageLabel;
	private FileInfo fileInfo;
	
	public FileImageLabelTreeItem(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	public FileImageLabelTreeItem(FileImageLabel fileImageLabel) {
		this.fileImageLabel = fileImageLabel;
		this.setWidget(fileImageLabel);
	}
	

	public void setFileImageLabel(FileImageLabel fileImageLabel) {
		this.fileImageLabel = fileImageLabel;
		this.setWidget(fileImageLabel);
	}

	public FileImageLabel getFileImageLabel() {
		return fileImageLabel;
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

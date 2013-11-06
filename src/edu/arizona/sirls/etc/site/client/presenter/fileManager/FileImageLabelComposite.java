package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

public class FileImageLabelComposite extends ImageLabelComposite {
	
	private FileImageLabelTreeItem fileTreeItem;
	
	public FileImageLabelComposite(FileImageLabelTreeItem fileTreeItem, String imageUri, String width,
			String height, String label) {
		super(imageUri, width, height, label);
		this.fileTreeItem = fileTreeItem;
	}

	public FileInfo getFileInfo() {
		return fileTreeItem.getFileInfo();
	}

	public FileImageLabelTreeItem getFileTreeItem() {
		return fileTreeItem;
	}
	
}

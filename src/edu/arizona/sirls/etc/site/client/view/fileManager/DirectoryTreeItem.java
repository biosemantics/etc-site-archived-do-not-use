package edu.arizona.sirls.etc.site.client.view.fileManager;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

public class DirectoryTreeItem extends FileImageLabelTreeItem {

	private String folderImage = "images//Folder.gif";
	
	public DirectoryTreeItem(String label, FileInfo fileInfo) {
		super(fileInfo);
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(this, folderImage, "19", "20", label);
		super.setFileImageLabelComposite(fileComposite);
	}
}

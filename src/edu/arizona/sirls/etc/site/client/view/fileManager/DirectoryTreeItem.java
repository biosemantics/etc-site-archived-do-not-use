package edu.arizona.sirls.etc.site.client.view.fileManager;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;

public class DirectoryTreeItem extends FileImageLabelTreeItem {

	private String folderImage = "images//Folder.gif";
	
	public DirectoryTreeItem(String name, String path) {
		super(name, path);
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(folderImage, "19", "20", name, path);
		super.setFileImageLabelComposite(fileComposite);
	}
}

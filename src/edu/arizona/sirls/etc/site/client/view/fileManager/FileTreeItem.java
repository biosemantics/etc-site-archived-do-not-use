package edu.arizona.sirls.etc.site.client.view.fileManager;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;

public class FileTreeItem extends FileImageLabelTreeItem {

	private String fileImage = "images//File.gif";
	
	public FileTreeItem(String name, String path) {
		super(name, path);
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(fileImage, "16", "20", name, path, true);
		super.setFileImageLabelComposite(fileComposite);
	}
	
}

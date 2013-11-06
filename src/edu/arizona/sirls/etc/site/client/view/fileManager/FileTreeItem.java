package edu.arizona.sirls.etc.site.client.view.fileManager;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;

public class FileTreeItem extends FileImageLabelTreeItem {

	private String fileImage = "images//File.gif";
	
	public FileTreeItem(String label, FileInfo fileInfo) {
		super(fileInfo);
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(this, fileImage, "16", "20", label);
		super.setFileImageLabelComposite(fileComposite);
	}
	
}

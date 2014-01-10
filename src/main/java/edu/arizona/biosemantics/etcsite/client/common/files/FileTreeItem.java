package edu.arizona.biosemantics.etcsite.client.common.files;

import edu.arizona.biosemantics.etcsite.shared.file.FileInfo;

public class FileTreeItem extends FileImageLabelTreeItem {

	private String fileImage = "images/File.gif";
	
	public FileTreeItem(String label, FileInfo fileInfo) {
		super(fileInfo);
		FileImageLabel fileComposite = new FileImageLabel(this, fileImage, "16px", "20px", label);
		super.setFileImageLabel(fileComposite);
	}
	
}

package edu.arizona.biosemantics.etcsite.client.common.files;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;

public class DirectoryTreeItem extends FileImageLabelTreeItem {

	private String folderImage = "images/Folder.gif";
	
	public DirectoryTreeItem(String label, FileInfo fileInfo) {
		super(fileInfo);
		boolean systemFolder = false;
		if(fileInfo.getDisplayFilePath().compareTo("")==0 || fileInfo.getDisplayFilePath().compareTo("Owned")==0||fileInfo.getDisplayFilePath().compareTo("Shared")==0)
			systemFolder = true;
		FileImageLabel fileComposite = new FileImageLabel(this, folderImage, "19px", "20px", label, systemFolder);
		super.setFileImageLabel(fileComposite);
	}
}

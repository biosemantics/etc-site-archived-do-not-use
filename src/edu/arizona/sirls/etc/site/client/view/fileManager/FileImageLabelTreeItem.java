package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileImageLabelComposite;

public class FileImageLabelTreeItem extends TreeItem {

	private FileImageLabelComposite fileImageLabelComposite;
	private String path;
	private String name;
	
	public FileImageLabelTreeItem(String name, String path) {
		this.name = name;
		this.path = path;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

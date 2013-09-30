package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;

public class FileImageLabelComposite extends ImageLabelComposite {
	
	private String path;
	private boolean isFile;
	
	public FileImageLabelComposite(String imageUri, String width,
			String height, String labelText, String path, boolean isFile) {
		super(imageUri, width, height, labelText);
		this.path = path;
		this.isFile = isFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	
	
	
}

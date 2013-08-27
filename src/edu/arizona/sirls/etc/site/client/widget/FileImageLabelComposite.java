package edu.arizona.sirls.etc.site.client.widget;

public class FileImageLabelComposite extends ImageLabelComposite {
	
	private String path;
	
	public FileImageLabelComposite(String imageUri, String width,
			String height, String labelText, String path) {
		super(imageUri, width, height, labelText);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}

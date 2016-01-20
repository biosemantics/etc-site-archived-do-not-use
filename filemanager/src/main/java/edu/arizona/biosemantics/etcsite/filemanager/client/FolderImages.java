package edu.arizona.biosemantics.etcsite.filemanager.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;

public interface FolderImages extends ClientBundle {

	
	@Source("green.png")
	ImageResource green();
	
	@Source("yellow.png")
	ImageResource yellow();
	
	@Source("orange.png")
	ImageResource orange();
	
}
package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.FileManagerPopup;

public class FileManagerPopupClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		FileManagerPopup fileManagerPopup = new FileManagerPopup();
		fileManagerPopup.setGlassEnabled(true); 
		fileManagerPopup.center(); 
		fileManagerPopup.show(); 
	}

}

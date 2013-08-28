package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.builder.dialog.LoadingPopup;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.FileManagerPopup;
import edu.arizona.sirls.etc.site.client.widget.ILoadListener;

public class FileManagerPopupClickHandler implements ClickHandler, ILoadListener {

	private LoadingPopup loadingPopup;
	private FileManagerPopup fileManagerPopup;
	
	@Override
	public void onClick(ClickEvent event) {
		loadingPopup = new LoadingPopup();
		loadingPopup.center();
		loadingPopup.show(); 
		
		fileManagerPopup = new FileManagerPopup();
		//fileManagerPopup.setGlassEnabled(true); 
		fileManagerPopup.addLoadListener(this);
	}

	@Override
	public void notifyLoadFinished(Widget widget) {
		loadingPopup.hide();
		fileManagerPopup.center(); 
		fileManagerPopup.show(); 
	}

}

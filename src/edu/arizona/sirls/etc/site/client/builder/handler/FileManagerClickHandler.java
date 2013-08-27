package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.HelpContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileManagerContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.FileManagerPopup;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.review.OTOPopup;

public class FileManagerClickHandler implements ClickHandler {

	@Override
	public synchronized void onClick(ClickEvent event) {
		FileManagerPopup fileManagerPopup = new FileManagerPopup();
		fileManagerPopup.setGlassEnabled(true); 
		fileManagerPopup.center(); 
		fileManagerPopup.show(); 
		
		/*
		Session session = Session.getInstance();
		PageBuilder pageBuilder = session.getPageBuilder();
		pageBuilder.setContentBuilder(FileManagerContentBuilder.getInstance());
		pageBuilder.build(); */
	}

}

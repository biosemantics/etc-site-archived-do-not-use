package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileManagerContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.FileManagerPopup;

public class FileManagerClickHandler implements ClickHandler {

	@Override
	public synchronized void onClick(ClickEvent event) {
		Session session = Session.getInstance();
		PageBuilder pageBuilder = session.getPageBuilder();
		pageBuilder.setContentBuilder(new FileManagerContentBuilder());
		pageBuilder.build();
	}

}

package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedOutHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartMenuBuilder;

public class LogoutButtonClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		Authentication.getInstance().destory();
		Session session = Session.getInstance();
		PageBuilder pageBuilder = session.getPageBuilder();
		pageBuilder.setMenuBuilder(new StartMenuBuilder());
		pageBuilder.setContentBuilder(new StartContentBuilder());
		pageBuilder.setHeaderBuilder(new LoggedOutHeaderBuilder());
		pageBuilder.build();
	}

}

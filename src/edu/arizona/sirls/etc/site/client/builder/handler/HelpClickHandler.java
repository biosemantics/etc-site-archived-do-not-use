package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.HelpContentBuilder;

public class HelpClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		Session session = Session.getInstance();
		PageBuilder pageBuilder = session.getPageBuilder();
		pageBuilder.setContentBuilder(new HelpContentBuilder());
		pageBuilder.build();
	}

}

package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import edu.arizona.sirls.etc.site.client.presenter.HelpPresenter;

public class HelpView extends Composite implements HelpPresenter.Display {

	public HelpView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"Here is a lot of help to support the user</div>");
		this.initWidget(htmlPanel);
	}
	
}

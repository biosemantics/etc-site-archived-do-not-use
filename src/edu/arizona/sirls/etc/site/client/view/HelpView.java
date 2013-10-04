package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import edu.arizona.sirls.etc.site.client.presenter.HelpPresenter;

public class HelpView extends Composite implements HelpPresenter.Display {

	public HelpView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='helpContent'></div></div>");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image videoImage = new Image("images/video.png");
		videoImage.addStyleName("videoImage");
		horizontalPanel.add(videoImage);
		horizontalPanel.add(new Label("Watch the video tutorial"));
		
		htmlPanel.addAndReplaceElement(horizontalPanel, "helpContent");
		initWidget(htmlPanel);
	}
	
}

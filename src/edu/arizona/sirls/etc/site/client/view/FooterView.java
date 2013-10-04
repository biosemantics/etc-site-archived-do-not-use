package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import edu.arizona.sirls.etc.site.client.presenter.FooterPresenter;

public class FooterView extends Composite implements FooterPresenter.Display{

	public FooterView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div id='footerBackground'></div>" +
				"<div id='footerText'>School of Information Resources and Library Science<br>1515 East First Street<br>Tucson, AZ 85719</div>" +
				"<div id='collaboratorsText'>Collaborators:</div>" + 
				"<div id='footerLogo1'></div>" +
				"<div id='footerLogo2'></div>");
		initWidget(htmlPanel);
	}
	
	

}

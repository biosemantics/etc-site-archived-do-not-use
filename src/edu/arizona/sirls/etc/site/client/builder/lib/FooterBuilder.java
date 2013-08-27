package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.builder.IFooterBuilder;

public class FooterBuilder implements IFooterBuilder {

	private static FooterBuilder instance;

	public static FooterBuilder getInstance() {
		if(instance == null)
			instance = new FooterBuilder();
		return instance;
	}
	
	private FooterBuilder() { } 
	
	@Override
	public void build() {
		/*Element footer = DOM.getElementById("footer");
		footer.setInnerHTML("<div id='footerBackground'></div>" +
				"<div id='footerText'>School of Information Resources and Library Science<br>1515 East First Street<br>Tucson, AZ 85719</div>" +
				"<div id='footerLogo1'></div>" +
				"<div id='footerLogo2'></div>");
		*/
		RootPanel footerPanel = RootPanel.get("footer");
		HTMLPanel footerHTMLPanel = new HTMLPanel("<div id='footerBackground'></div>" +
				"<div id='footerText'>School of Information Resources and Library Science<br>1515 East First Street<br>Tucson, AZ 85719</div>" +
				"<div id='footerLogo1'></div>" +
				"<div id='footerLogo2'></div>");
		footerPanel.add(footerHTMLPanel);
	}
}

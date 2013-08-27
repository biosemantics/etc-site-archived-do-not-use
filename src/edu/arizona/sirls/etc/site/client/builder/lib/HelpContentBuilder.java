package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;

public class HelpContentBuilder implements IContentBuilder {

	@Override
	public void build() {
		createHTML();
		createWidgets();
		
	}

	private void createWidgets() {
		
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'>Here is a lot of help to support the user</div>");
		
		//can't do that 
		//RootPanel contentPanel = RootPanel.get("content");
		//HTMLPanel contentHTMLPanel = new HTMLPanel("Help");
		//contentPanel.add(contentHTMLPanel);
	} 

}

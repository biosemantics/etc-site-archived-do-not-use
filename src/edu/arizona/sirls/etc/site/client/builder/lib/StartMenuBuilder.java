package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.builder.IHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.IMenuBuilder;

public class StartMenuBuilder implements IMenuBuilder {

	private static StartMenuBuilder instance;

	public static StartMenuBuilder getInstance() {
		if(instance == null)
			instance = new StartMenuBuilder();
		return instance;
	}
	
	private StartMenuBuilder() { } 
	
	@Override
	public void build() {
		//RootPanel menuPanel = RootPanel.get("menu");
		Element menu = DOM.getElementById("menu");
		//HTMLPanel menuHTMLPanel = new HTMLPanel(
		menu.setInnerHTML(
			"<div id='menuStart'>" +
						"<div id='etcPipelineText'></div>" +
						"<div id='features'>" +
						"<div id='featuresText'></div>" +
						"<div id='featuresFirstBullet' class='bullet'></div>" +
						"<div id='featuresFirstText'>Data Sharing</div>" +
						"<div id='featuresSecondBullet' class='bullet'></div>" +
						"<div id='featuresSecondText'>Begin at any step</div>" +
						"<div id='featuresThirdBullet' class='bullet'></div>" +
						"<div id='featuresThirdText'>Full pipeline creation</div>" +
						"</div>" +
						"<div id='menuStartText'>ETC toolbox consists of four tools that can be used as a pipe line that takes clean textual phenotypical " +
						"descriptions and outputs taxon-character data for taxon concept analysis. The tools may also be used separately if their required input data is available. " +
						"The tools are produced through a collaborative project (NSF-DBI-1147266) between the University of Arizona and University of California at Davis. </div>" +
						"</div>");
		//menuPanel.add(menuHTMLPanel); 
	}
}

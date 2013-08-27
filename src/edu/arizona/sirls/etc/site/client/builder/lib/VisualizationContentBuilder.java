package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;

public class VisualizationContentBuilder implements IContentBuilder {

	private static VisualizationContentBuilder instance;
	
	public static VisualizationContentBuilder getInstance() {
		if(instance == null)
			instance = new VisualizationContentBuilder();
		return instance;
	}

	@Override
	public void build() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'>Here are the visualization steps</div>");
	}

}

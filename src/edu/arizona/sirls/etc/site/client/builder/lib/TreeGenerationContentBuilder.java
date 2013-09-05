package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;

public class TreeGenerationContentBuilder implements IContentBuilder {

	@Override
	public void build() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'>Here are the tree generation steps</div>");
	}

}

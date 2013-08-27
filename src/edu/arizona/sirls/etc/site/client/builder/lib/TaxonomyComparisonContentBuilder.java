package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;

public class TaxonomyComparisonContentBuilder implements IContentBuilder {

	private static TaxonomyComparisonContentBuilder instance;
	
	public static TaxonomyComparisonContentBuilder getInstance() {
		if(instance == null)
			instance = new TaxonomyComparisonContentBuilder();
		return instance;
	}

	@Override
	public void build() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'>Here are the taxonomy comparison steps</div>");
	}

}

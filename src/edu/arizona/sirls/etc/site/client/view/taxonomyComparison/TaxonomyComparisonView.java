package edu.arizona.sirls.etc.site.client.view.taxonomyComparison;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import edu.arizona.sirls.etc.site.client.presenter.taxonomyComparison.TaxonomyComparisonPresenter;

public class TaxonomyComparisonView extends Composite implements TaxonomyComparisonPresenter.Display {

	public TaxonomyComparisonView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"Here are the taxonomy comparison steps</div>");
		this.initWidget(htmlPanel);
	}
	
}

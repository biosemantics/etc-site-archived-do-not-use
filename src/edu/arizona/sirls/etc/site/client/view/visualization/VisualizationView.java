package edu.arizona.sirls.etc.site.client.view.visualization;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import edu.arizona.sirls.etc.site.client.presenter.visualization.VisualizationPresenter;

public class VisualizationView extends Composite implements VisualizationPresenter.Display {

	public VisualizationView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"Here are the visualization steps</div>");
		this.initWidget(htmlPanel);
	}
}

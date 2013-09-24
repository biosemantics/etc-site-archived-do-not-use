package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public abstract class MatrixGenerationView extends Composite {

	public MatrixGenerationView() {		
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='matrixGenerationContent'></div></div>");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);		
		int i=0;
		for(Step step : Step.values()) {
			ImageLabelComposite stepEntry = new ImageLabelComposite("images/Enumeration_unselected_" + i++ + ".gif", "20px", "20px", step.toString());
			if(getStep().equals(step))
				stepEntry = new ImageLabelComposite("images/Enumeration_" + i++ + ".gif", "20px", "20px", step.toString());
			stepEntry.addStyleName("submenuEntry");
			horizontalPanel.add(stepEntry);
		}
		verticalPanel.add(this.getStepWidget());
		
		htmlPanel.addAndReplaceElement(verticalPanel, "matrixGenerationContent");
		initWidget(htmlPanel);
	}

	protected abstract Step getStep();

	protected abstract Widget getStepWidget();
	
}

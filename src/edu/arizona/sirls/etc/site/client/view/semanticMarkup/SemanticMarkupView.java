package edu.arizona.sirls.etc.site.client.view.semanticMarkup;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;

public abstract class SemanticMarkupView extends Composite {

	public SemanticMarkupView() {		
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='semanticMarkupContent'></div></div>");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		Label headingLabel = new Label("Matrix Generation");
		headingLabel.addStyleName("siteHeading");
		verticalPanel.add(headingLabel);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);		
		horizontalPanel.addStyleName("submenu");
		int i=0;
		for(TaskStageEnum step : TaskStageEnum.values()) {
			ImageLabelComposite stepEntry = new ImageLabelComposite("images/Enumeration_unselected_" + i++ + ".gif", "20px", "20px", step.displayName());
			if(getStep().equals(step))
				stepEntry = new ImageLabelComposite("images/Enumeration_" + i++ + ".gif", "20px", "20px", step.displayName());
			stepEntry.addStyleName("submenuEntry");
			horizontalPanel.add(stepEntry);
		}
		verticalPanel.add(this.getStepWidget());
		
		htmlPanel.addAndReplaceElement(verticalPanel, "semanticMarkupContent");
		initWidget(htmlPanel);
	}

	protected abstract TaskStageEnum getStep();

	protected abstract Widget getStepWidget();
	
}

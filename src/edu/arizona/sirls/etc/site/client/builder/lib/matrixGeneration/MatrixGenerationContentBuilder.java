package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.client.widget.ImageLabelComposite;

public class MatrixGenerationContentBuilder implements IContentBuilder {

	private static MatrixGenerationContentBuilder instance;
	private IStepBuilder contentForMatrixGenerationStepBuilder;
	
	public static MatrixGenerationContentBuilder getInstance(IStepBuilder contentForMatrixGenerationStepBuilder) {
		if(instance == null)
			instance = new MatrixGenerationContentBuilder();
		instance.setContentForMatrixGenerationStepBuilder(contentForMatrixGenerationStepBuilder);
		return instance;
	}

	private void setContentForMatrixGenerationStepBuilder(IStepBuilder contentForMatrixGenerationStepBuilder) {
		this.contentForMatrixGenerationStepBuilder = contentForMatrixGenerationStepBuilder;
	}

	@Override
	public void build() {
		createHTML();
		initWidgets();
	}

	private void initWidgets() {
		RootPanel matrixGenerationContent = RootPanel.get("matrixGenerationContent");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.add(new Label("Matrix Generation:"));
		
		int i=0;
		for(Step step : Step.values()) {
			ImageLabelComposite stepEntry = new ImageLabelComposite("images/Enumeration_unselected_" + i++ + ".gif", "20px", "20px", step.toString());
			if(contentForMatrixGenerationStepBuilder.getStep().equals(step))
				stepEntry = new ImageLabelComposite("images/Enumeration_" + i++ + ".gif", "20px", "20px", step.toString());
			stepEntry.addStyleName("submenuEntry");
			horizontalPanel.add(stepEntry);
		}
		
		matrixGenerationContent.add(verticalPanel);
		contentForMatrixGenerationStepBuilder.build(verticalPanel);
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'><div id='matrixGenerationContent'></div></div>");
	}

}

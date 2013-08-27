package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.review;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.FormatRequirementsClickHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.parse.ParseStepBuilder;

public class ReviewStepBuilder implements IStepBuilder {

	private static ReviewStepBuilder instance;
	
	public static ReviewStepBuilder getInstance() {
		if(instance == null)
			instance = new ReviewStepBuilder();
		return instance;
	}
	
	@Override
	public void build(Panel panel) {
		MatrixGenerationJob.getInstance().setReviewTermsLink("http://biosemantics.arizona.edu:8080/OTOLite/?uploadID=54");
		panel.add(new Label("Review Terms"));
		
		panel.add(new Label("Please review the terms learned by "));
		
		Anchor otoAnchor = new Anchor("visiting OTO");
		otoAnchor.addClickHandler(new OTOClickHandler());
		panel.add(otoAnchor);
		
		Button nextButton = new Button("Next");
		nextButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
				pageBuilder.setContentBuilder(MatrixGenerationContentBuilder.getInstance(ParseStepBuilder.getInstance()));
				pageBuilder.build();
			}
		});
		panel.add(nextButton);
	}

	@Override
	public Step getStep() {
		return Step.REVIEW_TERMS;
	}

}

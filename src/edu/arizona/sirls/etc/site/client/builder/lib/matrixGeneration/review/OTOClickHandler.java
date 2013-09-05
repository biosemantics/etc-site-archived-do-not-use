package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.review;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;

public class OTOClickHandler implements ClickHandler {

	private MatrixGenerationJob matrixGenerationJob;

	public OTOClickHandler(MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
	}

	@Override
	public void onClick(ClickEvent event) {
		OTOPopup otoPopup = new OTOPopup(matrixGenerationJob.getReviewTermsLink());
		otoPopup.setGlassEnabled(true); 
		otoPopup.center(); 
		otoPopup.show(); 
	}

}

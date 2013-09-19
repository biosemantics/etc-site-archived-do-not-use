package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.EventHandler;

public interface PreprocessMatrixGenerationEventHandler extends EventHandler {

	void onPreprocess(
			PreprocessMatrixGenerationEvent preprocessMatrixGenerationEvent);

}

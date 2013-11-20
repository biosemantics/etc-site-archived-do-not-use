package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.matrixGeneration.OutputMatrixGenerationView.Presenter;

public interface ProcessMatrixGenerationView {

	public interface Presenter {
		
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

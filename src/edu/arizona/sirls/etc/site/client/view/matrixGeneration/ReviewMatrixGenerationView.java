package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.matrixGeneration.OutputMatrixGenerationView.Presenter;

public interface ReviewMatrixGenerationView {

	public interface Presenter {
		void onNext();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

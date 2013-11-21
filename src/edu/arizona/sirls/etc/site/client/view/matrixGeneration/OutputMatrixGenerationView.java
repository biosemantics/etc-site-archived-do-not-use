package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.matrixGeneration.InputMatrixGenerationView.Presenter;

public interface OutputMatrixGenerationView {

	public interface Presenter {
		void onFileManager();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setOutput(String output);
	
}

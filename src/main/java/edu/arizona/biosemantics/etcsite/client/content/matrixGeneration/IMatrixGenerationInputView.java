package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMatrixGenerationInputView extends IsWidget {

	public interface Presenter {
		void onInputSelect();
		void onFileManager();
		void onNext();
		IMatrixGenerationInputView getView();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);

}

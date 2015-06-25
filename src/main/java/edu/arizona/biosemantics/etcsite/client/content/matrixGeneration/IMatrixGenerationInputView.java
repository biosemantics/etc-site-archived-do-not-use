package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMatrixGenerationInputView extends IsWidget {

	public interface Presenter {
		void onNext();
		IMatrixGenerationInputView getView();
		void setSelectedFolder(String fullPath, String shortendPath);
		void onInputSelect();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();
	boolean isInheritValues();
	boolean isGenerateAbsentPresent();
	String getTaxonGroup();

}

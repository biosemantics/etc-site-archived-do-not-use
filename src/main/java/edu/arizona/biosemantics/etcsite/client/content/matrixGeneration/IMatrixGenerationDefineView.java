package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMatrixGenerationDefineView extends IsWidget {

	public interface Presenter {
		void onNext();
		IMatrixGenerationDefineView getView();
		void setSelectedFolder(String fullPath, String shortendPath);
		void onInputSelect();
		void onOntologyInput();
		void onTermReviewInput();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();
	boolean isInheritValues();
	boolean isGenerateAbsentPresent();
	String getTaxonGroup();
	void setOntologyPath(String text);
	boolean hasOntologyPath();
	boolean hasInput();
	void setTermReviewPath(String text);
	boolean hasTermReview();
	boolean hasTaskName();

}

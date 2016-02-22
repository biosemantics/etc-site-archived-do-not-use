package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITaxonomyComparisonInputView extends IsWidget {

	public interface Presenter {
		void onNext();
		//void setSelectedFolder(String fullPath, String shortenedPath);
		ITaxonomyComparisonInputView getView();
		//void onInputSelect();
		void onOntologyInput();
		void onTermReviewInput2();
		void onTermReviewInput1();
		void setSelectedSerializedModels(String inputFolderPath1,
				String inputFolderPath2);
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	//void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();
	boolean hasTaskName();
	boolean hasOntologyPath();
	String getTaxonGroup();
	void setOntologyPath(String text);
	void setTermReviewPath1(String text);
	void setTermReviewPath2(String text);
	boolean hasTermReview1();
	boolean hasTermReview2();

}

package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITaxonomyComparisonDefineView extends IsWidget {

	public interface Presenter {
		void onNext();
		void setSelectedCleanTaxFolder(String fullPath, String shortenedPath);
		ITaxonomyComparisonDefineView getView();
		//void onInputSelect();
		void onOntologyInput();
		void onTermReviewInput2();
		void onTermReviewInput1();
		void setSelectedSerializedModels(String model1, String model2, String modelPath1, String modelPath2);
		void onExistingModel1();
		void onExistingModel2();
		void onCleanTaxFolder();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
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
	void setSerializedModels(String serializedModel1, String serializedModel2);
	void setCleanTaxPath(String shortenedPath);
	void setSerializedModel1(String serializedModelPath1);
	void setSerializedModel2(String serializedModelPath2);
	String getTaxonomy1Author();
	String getTaxonomy2Author();
	String getTaxonomy1Year();
	String getTaxonomy2Year();

}

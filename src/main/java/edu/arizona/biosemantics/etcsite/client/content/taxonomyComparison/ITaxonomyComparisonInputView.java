package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITaxonomyComparisonInputView extends IsWidget {

	public interface Presenter {
		void onNext();
		void setSelectedFolder(String fullPath, String shortenedPath);
		ITaxonomyComparisonInputView getView();
		void onInputSelect();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();

}

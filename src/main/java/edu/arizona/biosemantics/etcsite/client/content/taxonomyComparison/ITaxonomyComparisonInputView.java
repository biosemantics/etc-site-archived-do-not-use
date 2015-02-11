package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITaxonomyComparisonInputView extends IsWidget {

	public interface Presenter {
		void onInputSelect();
		void onFileManager();
		void onNext();
		ITaxonomyComparisonInputView getView();
	}
	  
	void setPresenter(Presenter presenter);
	String getTaskName();
	void setFilePath(String shortendPath);
	void setEnabledNext(boolean b);
	void resetFields();

}

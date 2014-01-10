package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.search.Search;

public interface ISearchView extends IsWidget {

	public interface Presenter {
		void onSearchButtonClicked(Search search);
		void onInputButtonClicked();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setEnabled(boolean value);
	void setInput(String input);
	
}

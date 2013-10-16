package edu.arizona.sirls.etc.site.client.annotationReview.view;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public interface SearchView {

	public interface Presenter {
		void onSearchButtonClicked(Search search);
		void onInputButtonClicked();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setEnabledSearch(boolean value);
	void setInput(String input);
	
}

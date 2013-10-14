package edu.arizona.sirls.etc.site.client.annotationReview.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.annotationReview.presenter.XMLEditorPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public interface AnnotationReviewView {

	public interface Presenter {
		void onSearchButtonClicked(Search search);
		void onInputButtonClicked();
		void onResultClicked(SearchResult searchResult);
	}

	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setInput(String input);
	void setResult(List<SearchResult> searchResult);
	XMLEditorPresenter getXMLEditorPresenter();
    void setEnabledSearch(boolean value);
    void setEnabledXMLEditor(boolean value);
	
}

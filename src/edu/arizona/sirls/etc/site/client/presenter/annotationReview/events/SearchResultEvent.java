package edu.arizona.sirls.etc.site.client.presenter.annotationReview.events;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class SearchResultEvent extends GwtEvent<SearchResultEventHandler> {

	public static Type<SearchResultEventHandler> TYPE = new Type<SearchResultEventHandler>();
	private List<SearchResult> result;
	
	public SearchResultEvent(List<SearchResult> result) {
		this.result = result;
	}

	@Override
	public Type<SearchResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchResultEventHandler handler) {
		handler.onSearchResult(this);
	}

	public List<SearchResult> getResult() {
		return result;
	}	

}

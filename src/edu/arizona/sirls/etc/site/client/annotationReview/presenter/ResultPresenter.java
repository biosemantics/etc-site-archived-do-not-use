package edu.arizona.sirls.etc.site.client.annotationReview.presenter;

import com.google.gwt.event.shared.HandlerManager;

import edu.arizona.sirls.etc.site.client.annotationReview.events.SearchResultEvent;
import edu.arizona.sirls.etc.site.client.annotationReview.events.SearchResultEventHandler;
import edu.arizona.sirls.etc.site.client.annotationReview.events.TargetEvent;
import edu.arizona.sirls.etc.site.client.annotationReview.view.ResultView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.ResultView.Presenter;
import edu.arizona.sirls.etc.site.client.annotationReview.view.AnnotationReviewView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.ResultViewImpl;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class ResultPresenter implements Presenter {

	private HandlerManager eventBus;
	private ResultView view;

	public ResultPresenter(HandlerManager eventBus, ResultView view) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		
		eventBus.addHandler(SearchResultEvent.TYPE, new SearchResultEventHandler() {
			@Override
			public void onSearchResult(SearchResultEvent searchResultEvent) {
				ResultPresenter.this.view.setResult(searchResultEvent.getResult());
			}
		});
	}

	@Override
	public void onTargetClicked(String target) {
		eventBus.fireEvent(new TargetEvent(target));
	}

}

package edu.arizona.sirls.etc.site.client.presenter.annotationReview;

import com.google.gwt.event.shared.HandlerManager;

import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.SearchResultEvent;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.SearchResultEventHandler;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.TargetEvent;
import edu.arizona.sirls.etc.site.client.view.annotationReview.ResultView;

public class ResultPresenter implements ResultView.Presenter {

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

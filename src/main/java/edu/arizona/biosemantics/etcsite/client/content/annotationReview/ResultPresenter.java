package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;


public class ResultPresenter implements IResultView.Presenter {

	private EventBus eventBus;
	private IResultView view;

	@Inject
	public ResultPresenter(@Named("AnnotationReview")EventBus eventBus, IResultView view) {
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

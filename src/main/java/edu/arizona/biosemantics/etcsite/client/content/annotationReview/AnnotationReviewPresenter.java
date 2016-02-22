package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

public class AnnotationReviewPresenter implements IAnnotationReviewView.Presenter {

	private IAnnotationReviewView view;
	private EventBus eventBus;
	private IResultView.Presenter resultPresenter;
	private ISearchView.Presenter searchPresenter;
	private IXMLEditorView.Presenter xmlEditorPresenter;

	@Inject
	public AnnotationReviewPresenter(@Named("AnnotationReview")EventBus eventBus, 
			IAnnotationReviewView view, IResultView.Presenter resultPresenter, 
			ISearchView.Presenter searchPresenter, IXMLEditorView.Presenter xmlEditorPresenter) {
		this.eventBus = eventBus;
		this.view = view;
		this.view.setPresenter(this);
		this.resultPresenter = resultPresenter;
		this.searchPresenter = searchPresenter;
		this.xmlEditorPresenter = xmlEditorPresenter;
	}

	@Override
	public IAnnotationReviewView getView() {
		return view;
	}


}

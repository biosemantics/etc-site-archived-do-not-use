package edu.arizona.sirls.etc.site.client.presenter.annotationReview;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.annotationReview.AnnotationReviewView;

public class AnnotationReviewPresenter implements Presenter, AnnotationReviewView.Presenter {

	private AnnotationReviewView view;
	private HandlerManager eventBus;

	public AnnotationReviewPresenter(HandlerManager eventBus, AnnotationReviewView view) {
		this.eventBus = eventBus;
		this.view = view;
		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

}

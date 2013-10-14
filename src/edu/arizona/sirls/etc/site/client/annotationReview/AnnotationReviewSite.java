package edu.arizona.sirls.etc.site.client.annotationReview;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.annotationReview.view.AnnotationReviewViewImpl;

public class AnnotationReviewSite implements EntryPoint {

	@Override
	public void onModuleLoad() {
		HandlerManager eventBus = new HandlerManager(null);
		Presenter presenter = new MySitePresenter(eventBus);
		presenter.go(RootLayoutPanel.get());
	}

}

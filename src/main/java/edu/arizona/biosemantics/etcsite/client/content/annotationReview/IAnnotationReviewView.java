package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface IAnnotationReviewView extends IsWidget {

	public interface Presenter {
		public IAnnotationReviewView getView();
	}

	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

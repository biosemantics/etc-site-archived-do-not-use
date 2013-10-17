package edu.arizona.sirls.etc.site.client.view.annotationReview;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public interface AnnotationReviewView {

	public interface Presenter {

	}

	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

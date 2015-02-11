package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public interface ITaxonomyComparisonReviewView extends IsWidget {

	public interface Presenter {
		void onNext();
		ITaxonomyComparisonReviewView getView();
		void setTask(Task task);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

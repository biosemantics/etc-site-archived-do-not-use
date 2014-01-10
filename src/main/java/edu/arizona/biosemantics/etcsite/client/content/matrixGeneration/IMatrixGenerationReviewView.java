package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface IMatrixGenerationReviewView extends IsWidget {

	public interface Presenter {
		void onNext();
		IMatrixGenerationReviewView getView();
		void setTask(Task task);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}

package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface ITaxonomyComparisonProcessView extends IsWidget {

	public interface Presenter {
		void onNext();
		void onTaskManager();
		ITaxonomyComparisonProcessView getView();
		void setTask(Task task);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setNonResumable();
	void setResumable();
	
}

package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.euler.alignment.client.EulerAlignmentView;

public interface ITaxonomyComparisonAlignView extends IsWidget {

	public interface Presenter {
		ITaxonomyComparisonAlignView getView();
		void setTask(Task task);
		boolean hasUnsavedChanges();
		void setUnsavedChanges(boolean value);
		void clearDialogs();
		void onSave();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	EulerAlignmentView getEulerAlignmentView();
	
}

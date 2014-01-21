package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface ISemanticMarkupHierarchyView extends IsWidget {

	public interface Presenter {

		void onNext();
		ISemanticMarkupHierarchyView getView();
		void setTask(Task task);
		
	}
	
	void setPresenter(Presenter presenter);

	HasWidgets getHasWidgets();
}

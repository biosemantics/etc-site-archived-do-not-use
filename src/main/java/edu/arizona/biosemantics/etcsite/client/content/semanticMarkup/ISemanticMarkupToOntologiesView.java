package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface ISemanticMarkupToOntologiesView extends IsWidget {

	public interface Presenter {
		void setTask(Task task);
		ISemanticMarkupToOntologiesView getView();
		void onNext();
	}

	void setPresenter(Presenter presenter);
	HasWidgets getToOntologiesContainer();
	HasWidgets getContextContainer();
	
}

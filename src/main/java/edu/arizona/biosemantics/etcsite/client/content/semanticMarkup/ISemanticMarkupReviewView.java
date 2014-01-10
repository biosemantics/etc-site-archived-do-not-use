package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface ISemanticMarkupReviewView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		IsWidget getView();

		void onNext();
		
	}
	
	void setPresenter(Presenter presenter);

	void setFrameUrl(String string);
	
}

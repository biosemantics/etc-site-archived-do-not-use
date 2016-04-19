package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.oto2.oto.client.Oto;

public interface ISemanticMarkupReviewView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		ISemanticMarkupReviewView getView();

		void onNext();

		void onSave();

		void removeSaveTimer();

		boolean hasUnsavedChanges();
		
	}
	
	void setPresenter(Presenter presenter);

	Oto getOto();
	
}

package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;

public interface ISemanticMarkupToOntologiesView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		ISemanticMarkupToOntologiesView getView();

		void onNext();
		
	}
	
	void setPresenter(Presenter presenter);

	Ontologize getOntologize();

	void setOntologize(int ontologizeUploadId, String ontologizeSecret);
	
}

package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.oto2.ontologize2.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

public interface IOntologizeBuildView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		IOntologizeBuildView getView();

		void onNext();

		void onAddInput();
		
	}
	
	void setPresenter(Presenter presenter);

	Ontologize getOntologize();

	void setOntologize(Collection collection);
	
}

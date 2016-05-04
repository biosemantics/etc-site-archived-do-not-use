package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.ucdavis.cs.cfgproject.client.KeyView;

public interface ITreeGenerationViewView extends IsWidget {

	public interface Presenter {
		ITreeGenerationViewView getView();
		void setTask(Task task);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	KeyView getKeyView();
	
}

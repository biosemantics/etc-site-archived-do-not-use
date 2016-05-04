package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface IOntologizeOutputView extends IsWidget {

	public interface Presenter {
		void setTask(Task task);
		void onFileManager();
		public IOntologizeOutputView getView();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setOutput(String output);
	
}

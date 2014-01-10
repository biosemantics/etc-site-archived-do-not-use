package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface IMatrixGenerationOutputView extends IsWidget {

	public interface Presenter {
		void setTask(Task task);
		void onFileManager();
		public IMatrixGenerationOutputView getView();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setOutput(String output);
	
}

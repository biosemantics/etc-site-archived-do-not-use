package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface IMatrixGenerationProcessView extends IsWidget {

	public interface Presenter {
		void onTaskManager();
		IMatrixGenerationProcessView getView();
		void setTask(Task task);
		void onReview();
		void onOutput();
		void onOutputMC();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setNonResumable();
	void setResumable();
	void setOutput(String value);
	
}

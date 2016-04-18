package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface ISemanticMarkupOutputView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		IsWidget getView();

		void onFileManager();

		void onSendToOto();

		void onContinueMatrixGeneration(String text);
		
	}
	
	void setPresenter(Presenter presenter);

	void setEnabledSendToOto(boolean value);

	void setOutput(String outputFull, String outputFullDisplay,
			String outputTermReview);
	
}

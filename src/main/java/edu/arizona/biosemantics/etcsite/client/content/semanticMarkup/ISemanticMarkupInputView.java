package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface ISemanticMarkupInputView extends IsWidget {

	public interface Presenter {

		IsWidget getView();

		void onNext();

		void onInput();

		void onFileManager();
		
	}
	
	void setPresenter(Presenter presenter);

	void setInput(String input);

	void setEnabledNext(boolean value);

	String getTaskName();

	String getGlossaryName();
	
	void resetFields();

	boolean isEmptyGlossarySelected();
	
}

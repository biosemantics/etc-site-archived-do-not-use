package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

public interface ISemanticMarkupDefineView extends IsWidget {

	public interface Presenter {

		IsWidget getView();

		void onNext();
		
		void setSelectedFolder(String fullPath, String shortendPath);

		void onInput();
		
	}
	
	void setPresenter(Presenter presenter);

	void setInput(String input);

	void setEnabledNext(boolean value);

	String getTaskName();

	String getGlossaryName();
	
	void resetFields();

	boolean isEmptyGlossarySelected();
	
}

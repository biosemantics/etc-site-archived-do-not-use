package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;

public interface ISemanticMarkupInputView extends IsWidget{
	
	public interface Presenter {

		IsWidget getView();

		void onFileManager();

		String getInputFolderPath();

		String getInputFolderShortenedPath();

		void refresh();
		
	}
	
	void setPresenter(Presenter presenter);

	IInputCreateView getInputCreateView();

}

package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;

public interface IMatrixGenerationInputView extends IsWidget {
	
	public interface Presenter {

		IsWidget getView();

		String getInputFolderPath();

		String getInputFolderShortenedPath();

		void onFileManager();

		void refresh();
		
	}
	
	void setPresenter(Presenter presenter);

	IInputCreateView getInputCreateView();
	
}

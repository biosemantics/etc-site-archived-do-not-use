package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;

public interface ITreeGenerationInputView extends IsWidget {

	public interface Presenter {
		IsWidget getView();
		
		String getInputFolderPath();
		
		String getInputFolderShortenedPath();
		
		void onFileManager();

		void refresh();
	}
	  
	public void setPresenter(Presenter presenter);
	
	public IInputCreateView getInputCreateView();

}

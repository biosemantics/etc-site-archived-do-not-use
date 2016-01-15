package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;


public interface ITaxonomyComparisonCreateView extends IsWidget {

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

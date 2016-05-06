package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;


public interface ITaxonomyComparisonInputView extends IsWidget {

	public interface Presenter {
		IsWidget getView();
		
		String getCleanTaxInputFolderPath();
		
		String getCleanTaxInputFolderShortenedPath();
		
		void onFileManager();

		void refresh();

		boolean isFromCleantax();

		boolean isFromSerializedModel();

		String getInputTaxonomy1();
		
		String getInputTaxonomy2();

		String getModelFolderPath1();

		String getModelFolderPath2();

		void onCleantax();
	}
	  
	public void setPresenter(Presenter presenter);
	
	public IInputCreateView getInputCreateView();
	

}

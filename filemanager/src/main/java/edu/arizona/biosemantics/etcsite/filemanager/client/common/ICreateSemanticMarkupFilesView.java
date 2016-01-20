package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.filemanager.shared.model.DescriptionEntry;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.TaxonIdentificationEntry;

public interface ICreateSemanticMarkupFilesView extends IsWidget {

	public interface Presenter {

		ICreateSemanticMarkupFilesView getView();

		void onCreate();

		void setDestinationFilePath(String destinationFilePath);
		
		int getFilesCreated();

		void init();

		void onBatch(String text);

		void setPreviewText(LinkedHashMap<String, String> map, String text);

	}

	void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter);

	String getAuthor();

	String getYear();

	String getTitleText();

	List<TaxonIdentificationEntry> getTaxonIdentificationEntries();
	
	List<DescriptionEntry> getDescriptionsList();

	String getStrainNumber(); 
	
	String getEqStrainNumbers(); 

	String getStrainAccession(); 

	void removeAddtionalTaxonRanks();

	String getDOI();

	String getFullCitation();

	void updateProgress(double value);

	void hideProgress();

	void showProgress();

	boolean isCopyCheckBox();

	void resetDescriptions();

	void setPreviewText(String text);

	void clearBatchText();
	
}

package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.file.DescriptionEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;

public interface ICreateSemanticMarkupFilesView extends IsWidget {

	public interface Presenter {

		ICreateSemanticMarkupFilesView getView();

		void onCreate();

		void setDestinationFilePath(String destinationFilePath);
		
		int getFilesCreated();

		void init();

		void onBatch(String text);

		void setPreviewText(String batchSourceDocumentInfo, String text);

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

	String getBatchSourceDocumentInfo();

	void clearBatchText();
	
}

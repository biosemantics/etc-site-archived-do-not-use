package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;

public interface ICreateSemanticMarkupFilesView extends IsWidget {

	public interface Presenter {

		ICreateSemanticMarkupFilesView getView();

		void onCreate();

		void setDestinationFilePath(String destinationFilePath);
		
		int getFilesCreated();

		void init();

		void onBatch(String text);

	}

	void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter);

	String getAuthor();

	String getYear();

	String getTitleText();

	List<TaxonIdentificationEntry> getTaxonIdentificationEntries();

	String getStrain();

	String getStrainsSource();

	String getMorphologicalDescription();

	String getPhenologyDescription();

	String getHabitatDescription();

	String getDistributionDescription();

	void removeAddtionalTaxonRanks();

	String getDOI();

	String getFullCitation();
	
}

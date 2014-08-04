package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.ProgressMessageBox;

import edu.arizona.biosemantics.etcsite.server.rpc.XmlModelFileCreator;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;


public class CreateSemanticMarkupFilesView extends Composite implements ICreateSemanticMarkupFilesView {

	private static CreateSemanticMarkupFilesViewUiBinder uiBinder = GWT.create(CreateSemanticMarkupFilesViewUiBinder.class);
	
	interface CreateSemanticMarkupFilesViewUiBinder extends UiBinder<Widget, CreateSemanticMarkupFilesView> { }
	
	@UiField
	TextBox doi;
	
	@UiField
	TextBox fullCitation;
	
	@UiField
	TextBox author;
	
	@UiField
	TextBox year;
	
	@UiField
	TextBox title;
	
	@UiField
	TextBox strain;
	
	@UiField
	TextBox equivalStrain;
	
	@UiField
	TextBox strainAccession;
	
	@UiField
	TextArea morphologicalDescription;
	
	@UiField
	TextArea phenologicalDescription;
	
	@UiField
	TextArea habitatDescription;
	
	@UiField
	TextArea distributionDescription;
	
	@UiField
	Button createButton;
	
	@UiField
	Grid ranksGrid;
	
	//@UiField
	//ListBox ranksList;
	
	@UiField
	SuggestBox ranksList;
	
	@UiField
	Button addRankButton;
	
	@UiField
	Button batchButton;
	
	@UiField
	TextArea batchArea;
	
	@UiField
	TabPanel tabPanel;
	
	ProgressMessageBox progressBox = new ProgressMessageBox("Creating...");
	
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	public CreateSemanticMarkupFilesView() {
		ranksList = initRanksListBox();
		initWidget(uiBinder.createAndBindUi(this));
		tabPanel.selectTab(0);
		//initRanksListBox(ranksList);
		//ranksList = initRanksListBox();
	}

	private SuggestBox initRanksListBox() {
		//reference http://en.wikipedia.org/wiki/Taxonomic_rank
		MultiWordSuggestOracle ranks = new MultiWordSuggestOracle();
		ranks.add("domain");
		ranks.add("subdomain");
		ranks.add("superkingdom");
		ranks.add("kingdom");
		ranks.add("subkingdom");
		ranks.add("superphylum");
		ranks.add("phylum");
		ranks.add("subphylum");
		ranks.add("superdivision");
		ranks.add("division");
		ranks.add("subdivision");
		ranks.add("superclass");
		ranks.add("class");
		ranks.add("subclass");
		ranks.add("superorder");
		ranks.add("order");
		ranks.add("suborder");
		ranks.add("superfamily");
		ranks.add("family");
		ranks.add("subfamily");
		ranks.add("supertribe");
		ranks.add("tribe");
		ranks.add("subtribe");
		ranks.add("supergenus");
		ranks.add("genus");
		ranks.add("subgenus");
		ranks.add("supersection");
		ranks.add("section");
		ranks.add("subsection");
		ranks.add("superseries");
		ranks.add("series");
		ranks.add("subseries");
		ranks.add("superseries");
		ranks.add("species group");
		ranks.add("species subgroup");
		ranks.add("species complex");
		ranks.add("superspecies");
		ranks.add("species");
		ranks.add("subspecies");
		ranks.add("supervariety");
		ranks.add("variety");
		ranks.add("subvariety");
		ranks.add("superforma");
		ranks.add("forma");
		ranks.add("subforma");
		ranks.add("unranked");
		return new SuggestBox(ranks);
		/*ranksList.addItem("class");
		ranksList.addItem("subclass");
		ranksList.addItem("order");
		ranksList.addItem("suborder");
		ranksList.addItem("superfamily");
		ranksList.addItem("family");
		ranksList.addItem("subfamily");
		ranksList.addItem("tribe");
		ranksList.addItem("subtribe");
		ranksList.addItem("genus");
		ranksList.addItem("subgenus");
		ranksList.addItem("section");
		ranksList.addItem("subsection");
		ranksList.addItem("series");
		ranksList.addItem("species");
		ranksList.addItem("subspecies");
		ranksList.addItem("variety");
		ranksList.addItem("subvarietas");
		ranksList.addItem("forma");
		ranksList.addItem("subforma");
		ranksList.addItem("unranked");*/
		//return ranksList;
	}

	@UiHandler("addRankButton")
	public void onAddRank(ClickEvent event) {
		int newrow = ranksGrid.insertRow(ranksGrid.getRowCount() - 1);
		//ListBox ranksBox = new ListBox();
		//initRanksListBox(ranksBox);
		SuggestBox ranksBox = initRanksListBox();
		/*if(newrow > 0)  {
			Widget widget = ranksGrid.getWidget(newrow -1, 0);
			if(widget instanceof ListBox) {
				ListBox previousRanksBox = (ListBox)widget;
				if(previousRanksBox.getSelectedIndex() < ranksBox.getItemCount() - 1)
					ranksBox.setItemSelected(previousRanksBox.getSelectedIndex() + 1, true);
			}
		}*/
		ranksGrid.setWidget(newrow, 0, ranksBox);
		ranksGrid.setWidget(newrow, 1, new TextBox());
	}
	
	@UiHandler("createButton") 
	public void onCreate(ClickEvent event) {
		presenter.onCreate();
	}

	public String getAuthor() {
		return author.getText().trim();
	}

	public String getYear() {
		return year.getText().trim();
	}

	public String getTitleText() {
		return title.getText().trim();
	}
	
	public String getStrainNumber() {
		return strain.getText().trim();
	}
	
	public String getEqStrainNumbers() {
		return equivalStrain.getText().trim();
	}

	public String getStrainAccession() {
		return strainAccession.getText().trim();
	}
	public List<TaxonIdentificationEntry> getTaxonIdentificationEntries() {
		List<TaxonIdentificationEntry> result = new LinkedList<TaxonIdentificationEntry>();
		for(int i = 1; i < ranksGrid.getRowCount() - 1; i++){ //row 0 is the header row, also there is a button at the end of table
			Widget rankWidget = ranksGrid.getWidget(i, 0);
			Widget valueWidget = ranksGrid.getWidget(i, 1);
			if(rankWidget instanceof SuggestBox && valueWidget instanceof TextBox) { 
				SuggestBox rankBox = (SuggestBox)rankWidget;
				//String rank = rankBox.getItemText(rankBox.getSelectedIndex());
				String rank = rankBox.getText();
				
				TextBox valueBox = (TextBox)valueWidget;
				String value = valueBox.getText().trim();
				result.add(new TaxonIdentificationEntry(rank, value));
			}
		}
		return result;
	}

	public String getMorphologicalDescription() {
		return morphologicalDescription.getText().trim();
	}

	public String getPhenologyDescription() {
		return phenologicalDescription.getText().trim();
	}

	public String getHabitatDescription() {
		return habitatDescription.getText().trim();
	}

	public String getDistributionDescription() {
		return distributionDescription.getText().trim();
	}
	
	public String getDOI() {
		return this.doi.getText().trim();
	}
	
	public String getFullCitation() {
		return this.fullCitation.getText().trim();
	}

	@Override
	public void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void removeAddtionalTaxonRanks() {
		while(ranksGrid.getRowCount() > 3) {
			ranksGrid.removeRow(ranksGrid.getRowCount() - 2);
		}
	}
	
	@UiHandler("batchButton")
	public void onBatch(ClickEvent event) {
		this.presenter.onBatch(this.batchArea.getText());
	}
	
	@Override
	public synchronized void incrementProgress(double value) {
		progressBox.updateProgress(value, "please wait...");
		progressBox.show();
	}
	
	@Override
	public void hideProgress() {
		progressBox.hide();
	}
	
	

}

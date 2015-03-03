package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.box.ProgressMessageBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;


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
		
	@UiField(provided=true)
	ComboBox<Rank> ranksCombo;
	
	@UiField
	Button addRankButton;
	
	@UiField
	Button batchButton;
	
	@UiField
	TextArea batchArea;
	
	@UiField
	TabPanel tabPanel;
	
	@UiField
	DisclosurePanel strainPanel;
	
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	private ProgressMessageBox progressBox;

	public CreateSemanticMarkupFilesView() {
	    ListStore<Rank> store = new ListStore<Rank>(new ModelKeyProvider<Rank>() {
			@Override
			public String getKey(Rank item) {
				return String.valueOf(item.getId());
			}
	    });
	    store.addAll(Arrays.asList(Rank.values()));
	 
	    ranksCombo = new ComboBox<Rank>(store, new LabelProvider<Rank>() {
			@Override
			public String getLabel(Rank item) {
				return item.name();
			}
	    });
	    ranksCombo.setAllowBlank(false);
	    ranksCombo.setForceSelection(true);
	    ranksCombo.setTriggerAction(TriggerAction.ALL);
	    ranksCombo.setValue(Rank.LIFE);
		
		initWidget(uiBinder.createAndBindUi(this));
		tabPanel.selectTab(0);
	}

	@UiHandler("addRankButton")
	public void onAddRank(ClickEvent event) {
		if(currentRankIsStrain()) {
			strainPanel.setVisible(true);
		}
		
		int newRow = ranksGrid.insertRow(ranksGrid.getRowCount() - 1);
		Label label = new Label(ranksCombo.getValue().name());
		ranksGrid.setWidget(newRow - 1, 0, label);
		ranksGrid.setWidget(newRow, 0, ranksCombo);
		ranksGrid.setWidget(newRow, 1, new TextField());
		ranksCombo.setValue(Rank.values()[ranksCombo.getValue().getId() + 1]);
		
	}
	
	private boolean currentRankIsStrain() {
		Rank rank = ranksCombo.getValue();
		Set<Rank> strainRanks = new HashSet<Rank>();
		strainRanks.add(Rank.STRAIN);
		strainRanks.add(Rank.SUPERSTRAIN);
		strainRanks.add(Rank.SUBSTRAIN);
		strainRanks.add(Rank.SUPERTYPESTRAIN);
		strainRanks.add(Rank.TYPESTRAIN);
		strainRanks.add(Rank.SUBTYPESTRAIN);
		return strainRanks.contains(rank);
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
			
			Rank rank = null;
			
			if(rankWidget instanceof ComboBox) {
				ComboBox<Rank> rankBox = (ComboBox)rankWidget;
				//String rank = rankBox.getItemText(rankBox.getSelectedIndex());
				rank = rankBox.getValue();
			}
			if(rankWidget instanceof Label) {
				Label rankLabel = (Label)rankWidget;
				rank = Rank.valueOf(rankLabel.getText());
			}
			if(valueWidget instanceof TextField) {
				TextField valueBox = (TextField)valueWidget;
				String value = valueBox.getText().trim();
				if(rank != null)
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
		ranksCombo.setValue(Rank.LIFE);
		ranksGrid.setWidget(1, 0, ranksCombo);
	}
	
	@UiHandler("batchButton")
	public void onBatch(ClickEvent event) {
		this.presenter.onBatch(this.batchArea.getText());
	}
	
	@Override
	public void updateProgress(final double value) {
		 progressBox.updateProgress(value, "{0}% Complete");
	}
	
	@Override
	public void hideProgress() {
		progressBox.hide();
	}

	@Override
	public void showProgress() {
		progressBox = new ProgressMessageBox("Creating...", "Creating your files, please wait...");
        progressBox.setProgressText("Initializing...");
        progressBox.setPredefinedButtons();
        progressBox.show();
	}

}

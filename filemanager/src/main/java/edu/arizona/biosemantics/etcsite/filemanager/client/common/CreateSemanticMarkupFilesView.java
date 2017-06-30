package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.box.ProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.common.taxonomy.Description;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.DescriptionEntry;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.TaxonIdentificationEntry;

@SuppressWarnings("unchecked")
public class CreateSemanticMarkupFilesView extends Composite implements ICreateSemanticMarkupFilesView {

	private static CreateSemanticMarkupFilesViewUiBinder uiBinder = GWT.create(CreateSemanticMarkupFilesViewUiBinder.class);
	
	interface CreateSemanticMarkupFilesViewUiBinder extends UiBinder<Widget, CreateSemanticMarkupFilesView> { }
	
	/*================= Layout, Panel, Container Fields =================*/
	@UiField
	VBoxLayoutContainer buttonBox;
	
	@UiField
	CardLayoutContainer layout;
	
	@UiField
	CardLayoutContainer cardContainer;
	
	@UiField
	CardLayoutContainer batchCreateCards;
	
	@UiField
	TabPanel tabPanel;
	
	@UiField
	VerticalPanel strainPanel;
	
	@UiField
	VerticalPanel batchPanel;
	
	@UiField
	VerticalPanel previewPanel;
	
	@UiField
	HorizontalPanel buttonPanel;
	
	@UiField
	Grid docInfoGrid;

	/*================= Buttons in containing panel (Tab Panel) =================*/
	@UiField
	Button showInstructions;
	
	@UiField
	Button showExamples;
	
	@UiField
	ToggleButton basic, roFields, tNames, descInstructions;
		
	/*================= Single File Creation Fields =================*/
	@UiField
	TextBox author;
	
	@UiField
	TextBox year;
	
	@UiField
	TextBox title;
	
	@UiField
	TextBox doi;
	
	@UiField
	TextBox fullCitation;
	
	@UiField
	Grid ranksGrid;
		
	@UiField(provided=true)
	ComboBox<Rank> ranksCombo;
	
	@UiField
	Button addRankButton;

	@UiField
	Button authorityDateButton;
	
	@UiField
	TextBox strain;
	
	@UiField
	TextBox equivalStrain;
	
	@UiField
	TextBox strainAccession;	
		
	@UiField
	Button addDescriptionButton;
	
	@UiField
	Grid descriptionGrid;
	
	@UiField(provided=true)
	ComboBox<Description> descriptionCombo;
	
	@UiField(provided=true)
	ComboBox<String> scopeCombo;
	
	@UiField
	TextBox scopeTextBox;
	
	@UiField
	VerticalPanel scopePanel;
	
	@UiField
	TextArea descriptionArea;
	
	@UiField
	Button createButton;

	/*================= Batch Creation Fields =================*/
	@UiField
	TextBox batch_author;
	
	@UiField
	TextBox batch_year;
	
	@UiField
	TextBox batch_title;
	
	@UiField
	TextBox batch_doi;
	
	@UiField
	TextBox batch_fullCitation;
	
	@UiField
	CheckBox copyCheckBox;
	
	@UiField
	TextArea batchArea;
	
	@UiField
	TextArea previewArea;
	
	@UiField
	Button batchButton;
	
	@UiField
	Button previewButton;
	
	@UiField
	Button returnButton;
	
	String authorityDate;
	
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	private ProgressMessageBox progressBox;

	public CreateSemanticMarkupFilesView() {
		
		authorityDate="unknown,unknown";
		
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
	    ranksCombo.setValue(Rank.DOMAIN);
		
	    ListStore<Description> descStore = new ListStore<Description>(new ModelKeyProvider<Description>() {
			@Override
			public String getKey(Description item) {
				return String.valueOf(item.getId());
			}
	    });
	    descStore.addAll(Arrays.asList(Description.values()));
	 
	    descriptionCombo = new ComboBox<Description>(descStore, new LabelProvider<Description>() {
			@Override
			public String getLabel(Description item) {
				return item.name();
			}
	    });
	    descriptionCombo.setAllowBlank(false);
	    descriptionCombo.setForceSelection(true);
	    descriptionCombo.setTriggerAction(TriggerAction.ALL);
	    descriptionCombo.setValue(Description.MORPHOLOGY);
	    
	    
	    ListStore<String> scopeList = new ListStore<String>(new ModelKeyProvider<String>() {

			@Override
			public String getKey(String item) {
				return item;
			}
		});
	    scopeList.add("male");
	    scopeList.add("female");
	    scopeList.add("larva");
	    scopeList.add("worker");
	    scopeList.add("queen");
	    scopeList.add("Other");
	    
	    scopeCombo = new ComboBox<String>(scopeList, new LabelProvider<String>() {
			@Override
			public String getLabel(String item) {
				return item;
			}
	    });
	    
	    scopeCombo.addSelectionHandler(new SelectionHandler<String>() {
			
			@Override
			public void onSelection(SelectionEvent<String> event) {
				if(event.getSelectedItem().equals("Other")){
					scopeTextBox.setVisible(true);
				}else{
					scopeTextBox.setVisible(false);
				}
			}
		});
	    
		initWidget(uiBinder.createAndBindUi(this));
		
		ToggleGroup toggleGroup = new ToggleGroup();
	    toggleGroup.add(basic);
	    toggleGroup.add(roFields);
	    toggleGroup.add(tNames);
	    toggleGroup.add(descInstructions);
		tabPanel.selectTab(0);
		
		author.getElement().setPropertyString("placeholder", "Enter only the Last Name of the first Author here");
		title.getElement().setPropertyString("placeholder", "Enter Publication Title");
		doi.getElement().setPropertyString("placeholder", "Enter DoI");
		fullCitation.getElement().setPropertyString("placeholder", "Enter Full Citation");
		year.getElement().setPropertyString("placeholder", "Enter Publication Year");
		
		batch_author.getElement().setPropertyString("placeholder", "Enter only the Last Name of the first Author here");
		batch_title.getElement().setPropertyString("placeholder", "Enter Publication Title");
		batch_doi.getElement().setPropertyString("placeholder", "Enter DoI");
		batch_fullCitation.getElement().setPropertyString("placeholder", "Enter Full Citation");
		batch_year.getElement().setPropertyString("placeholder", "Enter Publication Year");
		
	}
 
	/*============ Handler Methods ============*/
	@UiHandler({
	      "basic", "roFields", "tNames", "descInstructions"})
	public void buttonClicked(SelectEvent event) {
	    ToggleButton button = (ToggleButton) event.getSource();
	 
	    int index = buttonBox.getWidgetIndex(button);
	    layout.setActiveWidget(layout.getWidget(index));
	}
	  
	@UiHandler({
	      "showInstructions", "showExamples"})
	public void onButtonClick(ClickEvent event) {
		  Button source = (Button)event.getSource();
		  int index = buttonPanel.getWidgetIndex(source);
		  cardContainer.setActiveWidget(cardContainer.getWidget(index));
	}
	  
	@UiHandler("previewButton")
	public void onPreview(ClickEvent event) {
		if(!batchArea.getText().equals("")){
			this.presenter.setPreviewText(getBatchSourceDocumentInfo(), this.batchArea.getText());
			if(!previewArea.getText().equals("")){
				batchCreateCards.setActiveWidget(previewPanel);  
			}
		}else{
			Alerter.inputError("No input text provided.");
		}
		
	}
	  
	@UiHandler("returnButton")
	public void onReturn(ClickEvent event) {
		  batchCreateCards.setActiveWidget(batchPanel);
	}
	 
	@UiHandler("addRankButton")
	public void onAddRank(ClickEvent event) {
		if(currentRankIsStrain()) {
			strainPanel.setVisible(true);
		}
		
		Widget valueWidget = ranksGrid.getWidget(ranksGrid.getRowCount()-2, 1);
		TextField tf = (TextField)valueWidget;
		String tfValue = tf.getText();
		tfValue = tfValue.trim();
		String splits[] = tfValue.split(" ", 2);
		if(splits.length>1){
			authorityDate = splits[1].trim();
		}
		
		int newRow = ranksGrid.insertRow(ranksGrid.getRowCount() - 1);
		Label label = new Label(ranksCombo.getValue().name());
		ranksGrid.setWidget(newRow - 1, 0, label);
		ranksGrid.remove(authorityDateButton);
		ranksGrid.setWidget(newRow, 0, ranksCombo);
		ranksGrid.setWidget(newRow, 1, new TextField());
		ranksCombo.setValue(Rank.values()[ranksCombo.getValue().getId() + 1]);
		
		ranksGrid.setWidget(newRow, 2, authorityDateButton);
		authorityDateButton.setVisible(true);	
	}
	
	@UiHandler("addDescriptionButton")
	public void onAddDescription(ClickEvent event) {
		int newRow = descriptionGrid.insertRow(descriptionGrid.getRowCount()-1);
		TextArea tArea = new TextArea();
		tArea.setStyleName(descriptionArea.getStyleName());
		Label descriptionLabel = new Label(descriptionCombo.getValue().name());
		Label scopeLabel = new Label();
		if(scopeCombo.getText().equals("Other")){
			scopeLabel.setText(scopeTextBox.getText());
		}else{
			scopeLabel.setText(scopeCombo.getText());
		}
		scopeTextBox.setVisible(false);
		scopeCombo.clear();
		descriptionGrid.setWidget(newRow-1, 0, descriptionLabel);
		descriptionGrid.setWidget(newRow-1, 1, scopeLabel);
		descriptionGrid.setWidget(newRow, 0, descriptionCombo);
		descriptionGrid.setWidget(newRow, 1, scopePanel);
		descriptionGrid.setWidget(newRow, 2, tArea);
	}
	
	@UiHandler("authorityDateButton")
	public void onAddAuthorityDate(ClickEvent event) {
		Widget valueWidget = ranksGrid.getWidget(ranksGrid.getRowCount()-2, 1);
		TextField tf = (TextField)valueWidget;
		String tfValue = tf.getText();
		tfValue = tfValue.trim();
		if(!hasAuthorityDate(tfValue)){
			tf.setText(tfValue+" "+authorityDate);
		}else{
			Alerter.inputError("Taxon Name already contains Authority and Date.");
		}
	}

	@UiHandler("createButton") 
	public void onCreate(ClickEvent event) {
		presenter.onCreate();
	}

	@UiHandler("batchButton")
	public void onBatch(ClickEvent event) {
		this.presenter.onBatch(this.previewArea.getText());
	}
	
	
	/*================= Getter Setter Methods =================*/
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
				ComboBox<Rank> rankBox = (ComboBox<Rank>)rankWidget;
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
	
	public List<DescriptionEntry> getDescriptionsList(){
		List<DescriptionEntry> entries = new LinkedList<DescriptionEntry>();
		for(int i=1; i<descriptionGrid.getRowCount()-1; i++){
			Widget typeWidget = descriptionGrid.getWidget(i, 0);
			Widget scopeWidget = descriptionGrid.getWidget(i,  1);
			Widget descriptionWidget = descriptionGrid.getWidget(i, 2);
			
			Description type = null;
			String scope = "";
			
			if(typeWidget instanceof ComboBox){
				ComboBox<Description> descCombo = (ComboBox<Description>)typeWidget;
				type = descCombo.getValue();
			}
			if(typeWidget instanceof Label){
				Label typeLabel = (Label)typeWidget;
				type = Description.valueOf(typeLabel.getText());
			}
			if(scopeWidget instanceof VerticalPanel){
				if(scopeCombo.getText().equals("Other")){
					scope = scopeTextBox.getText().trim();
				}else{
					scope = scopeCombo.getText().trim();
				}
			}
			if(scopeWidget instanceof Label){
				Label scopeLabel = (Label)scopeWidget;
				scope = scopeLabel.getText();
			}
			if(descriptionWidget instanceof TextArea){
				String desc = ((TextArea) descriptionWidget).getText().trim();
				if(type!=null){
					entries.add(new DescriptionEntry(type, scope, desc));
				}
			}
		}
		return entries;
	}
	
	public String getDOI() {
		return this.doi.getText().trim();
	}
	
	public String getFullCitation() {
		return this.fullCitation.getText().trim();
	}
	
	@Override
	public boolean isCopyCheckBox() {
		return copyCheckBox.getValue();
	}
	
	private LinkedHashMap<String, String> getBatchSourceDocumentInfo() {
		LinkedHashMap<String, String> sourceDocumentInfoMap = new LinkedHashMap<String, String>();
		sourceDocumentInfoMap.put("author", batch_author.getText());
		sourceDocumentInfoMap.put("year", batch_year.getText());
		sourceDocumentInfoMap.put("title", batch_title.getText());
		sourceDocumentInfoMap.put("doi", batch_doi.getText());
		sourceDocumentInfoMap.put("full citation", batch_fullCitation.getText());
		return sourceDocumentInfoMap;
	}
	
	@Override
	public void setPreviewText(String text){
		previewArea.setText(text);
	}

	@Override
	public void setPresenter(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.presenter = presenter;
	}
	
	/*================= Public Utility Methods =================*/
	@Override
	public void removeAddtionalTaxonRanks() {
		while(ranksGrid.getRowCount() > 3) {
			ranksGrid.removeRow(ranksGrid.getRowCount() - 2);
		}
		ranksCombo.setValue(Rank.DOMAIN);
		ranksGrid.setWidget(1, 0, ranksCombo);
	}
	
	@Override
	public void resetDescriptions() {
		descriptionCombo.setValue(Description.MORPHOLOGY);
		descriptionArea.setText("");
		descriptionGrid.setWidget(1, 0, descriptionCombo);
		while(descriptionGrid.getRowCount() > 3) {
			descriptionGrid.removeRow(descriptionGrid.getRowCount() - 2);
		}
	}
	
	@Override
	public void clearBatchText() {
		previewArea.setText("");
		batchArea.setText("");
		batchCreateCards.setActiveWidget(batchPanel);		
	}
	
	/*================= Progress Methods =================*/
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
	
	/*================= Private Methods =================*/
	private boolean currentRankIsStrain() {
		Rank rank = ranksCombo.getValue();
		Set<Rank> strainRanks = new HashSet<Rank>();
		//hong 618
		/*strainRanks.add(Rank.STRAIN);
		strainRanks.add(Rank.SUPERSTRAIN);
		strainRanks.add(Rank.SUBSTRAIN);
		strainRanks.add(Rank.SUPERTYPESTRAIN);
		strainRanks.add(Rank.TYPESTRAIN);
		strainRanks.add(Rank.SUBTYPESTRAIN);*/
		return strainRanks.contains(rank);
	}
	
	private boolean hasAuthorityDate(String tfValue) {
		String splits[] = tfValue.split(" ", 2);
		if(splits.length>1){
			return true;
		}
		return false;
	}

}

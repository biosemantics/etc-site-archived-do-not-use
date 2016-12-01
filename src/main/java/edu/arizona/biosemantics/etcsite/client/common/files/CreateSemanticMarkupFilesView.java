package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
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
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.common.taxonomy.Description;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.shared.model.file.DescriptionEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;

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
	Button deleteRankButton;
	
	@UiField
	Button addRankButton;

	@UiField
	Button authorityDateButton;
	
	@UiField 
	RadioButton createStrainRadio;
	
	@UiField
	TextBox strainNumber;
	
	@UiField
	TextBox equivalStrain;
	
	@UiField
	TextBox strainAccession;
	
	@UiField
	TextBox strainGenomeAccession;
	
	@UiField
	TextBox alternativeTaxonomy;
		
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
	
	int k=1;
	
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	private ProgressMessageBox progressBox;

	public CreateSemanticMarkupFilesView() {
		authorityDate="unspecified,unspecified";
		
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
		
		author.getElement().setPropertyString("placeholder", "Enter only the Last Name of the first Author");
		title.getElement().setPropertyString("placeholder", "Enter Publication Title");
		doi.getElement().setPropertyString("placeholder", "Enter DoI");
		fullCitation.getElement().setPropertyString("placeholder", "Enter Full Citation");
		year.getElement().setPropertyString("placeholder", "Enter Publication Year");
		strainNumber.getElement().setPropertyString("placeholder", "Enter strain number");
		equivalStrain.getElement().setPropertyString("placeholder", "Enter alternative strain name(s) with comma separated");
		strainAccession.getElement().setPropertyString("placeholder", "Enter accession number 16S rRNA");
		strainGenomeAccession.getElement().setPropertyString("placeholder", "Enter accession number for genome sequence");
		alternativeTaxonomy.getElement().setPropertyString("placeholder", "Enter previous or new taxonomic names");
		
		batch_author.getElement().setPropertyString("placeholder", "Enter only the Last Name of the first Author here");
		batch_title.getElement().setPropertyString("placeholder", "Enter Publication Title");
		batch_doi.getElement().setPropertyString("placeholder", "Enter DoI");
		batch_fullCitation.getElement().setPropertyString("placeholder", "Enter Full Citation");
		batch_year.getElement().setPropertyString("placeholder", "Enter Publication Year");
		
		batchArea.setHeight(400);
		batchArea.setWidth(720);
		previewArea.setHeight(400);
		previewArea.setWidth(720);
		descriptionArea.setWidth(550);
		descriptionArea.setHeight(60);
		batchArea.setContextMenu(createMenu(batchArea));
		descriptionArea.setContextMenu(createMenu(descriptionArea));
	}
 
	private Menu createMenu(final TextArea area) {
		Menu menu = new Menu();
		MenuItem insert = new MenuItem("Insert");
		Menu sub = new Menu();
		insert.setSubMenu(sub);
		for(final String symbol : new String[] { "°", "μm" }) {
			MenuItem item = new MenuItem(symbol);
			item.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					area.setValue(
							area.getText().substring(0, area.getCursorPos()) + 
							symbol + 
							area.getText().substring(area.getCursorPos()));
				}
			});
			sub.add(item);
		}
		menu.add(insert);
		return menu;
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
	
	/*@UiHandler("deleteRankButton")
	public void ondeleteRank(ClickEvent event) {
		ranksGrid.removeRow(ranksGrid.getCellForEvent(event).getRowIndex());
	}*/
	
	 
	@UiHandler("addRankButton")
	public void onAddRank(ClickEvent event) {
		/*if(currentRankIsStrain()) {
			strainPanel.setVisible(true);
		}*/
		Widget valueWidget1 = ranksGrid.getWidget(ranksGrid.getRowCount()-2, 1);
	    TextBox tf1 = (TextBox)valueWidget1;
		if(!ranksCombo.getValue().name().isEmpty()&&!tf1.getText().isEmpty()){
			int newRow = ranksGrid.insertRow(ranksGrid.getRowCount() - 1);
			Label label = new Label(ranksCombo.getValue().name());
		    deleteRankButton = new Button("Remove");
		    deleteRankButton.addClickHandler(new ClickHandler() {
		    	public void onClick(ClickEvent event) {
		    		ranksGrid.removeRow(ranksGrid.getCellForEvent(event).getRowIndex());
		    		if(ranksGrid.getRowCount()==3) 
		    			ranksGrid.remove(authorityDateButton);
		    	}
		    });
		    ranksGrid.setWidget(newRow - 1, 0, label);
		    ranksGrid.setWidget(newRow - 1, 2, deleteRankButton);
		    tf1.setEnabled(false);
		    ranksGrid.setWidget(newRow - 1, 1, tf1);
		    ranksGrid.remove(authorityDateButton);
		    ranksGrid.setWidget(newRow, 0, ranksCombo);
		    ranksGrid.setWidget(newRow, 1, new TextBox());
		    ranksCombo.setValue(Rank.values()[ranksCombo.getValue().getId() + 1]);
		    ranksGrid.setWidget(newRow, 2, authorityDateButton);
		    authorityDateButton.setVisible(true);	
		}
		else 
			Alerter.inputError("Please input rank information!");
	}
	
	@UiHandler("createStrainRadio")
	public void onCreateStrainRadion(ClickEvent event){
		if(k==1){
			strainPanel.setVisible(true);
			k=2;
		}
		else {
			resetStrain();
			k=1;
		}
	}
	
	@UiHandler("addDescriptionButton")
	public void onAddDescription(ClickEvent event) {
		Widget descriptionWidget = descriptionGrid.getWidget(descriptionGrid.getRowCount()-2, 2);
	    TextArea tArea1=(TextArea)descriptionWidget;
		if(!tArea1.getText().isEmpty()&&!descriptionCombo.getValue().name().isEmpty()) {
			int newRow = descriptionGrid.insertRow(descriptionGrid.getRowCount()-1);
		    TextArea tArea = new TextArea();
		    tArea.setWidth(550);
		    tArea.setHeight(60);
		    tArea.setContextMenu(createMenu(tArea));
			descriptionArea.setContextMenu(createMenu(descriptionArea));
		    tArea.setStyleName(descriptionArea.getStyleName());
		    Label descriptionLabel = new Label(descriptionCombo.getValue().name());
		    Label scopeLabel = new Label();
		    if(scopeCombo.getText().equals("Other")){
		    	scopeLabel.setText(scopeTextBox.getText());
		    }
		    else 
		    	scopeLabel.setText(scopeCombo.getText());
		    tArea1.setEnabled(false);
		    scopeTextBox.setVisible(false);
		    scopeCombo.clear();
		    descriptionGrid.setWidget(newRow-1, 0, descriptionLabel);
		    descriptionGrid.setWidget(newRow-1, 1, scopeLabel);
		    descriptionGrid.setWidget(newRow-1, 2, tArea1);
		    descriptionGrid.setWidget(newRow, 0, descriptionCombo);
		    descriptionGrid.setWidget(newRow, 1, scopeCombo);
		    descriptionGrid.setWidget(newRow, 2, tArea);
		}
		else
			Alerter.inputError("Please input description information!");
			
	}
	
	@UiHandler("authorityDateButton")
	public void onAddAuthorityDate(ClickEvent event) {
		Widget valueWidget1 = ranksGrid.getWidget(ranksGrid.getRowCount()-3, 1);
		TextBox tf1 = (TextBox)valueWidget1;
		String tfValue1 = tf1.getText();
		tfValue1 = tfValue1.trim();
		String splits[] = tfValue1.split(" ", 2);
		if(splits.length>1){
			authorityDate = splits[1].trim();
		}
		else authorityDate ="unspecified,unspecified";
		
		Widget valueWidget = ranksGrid.getWidget(ranksGrid.getRowCount()-2, 1);
		TextBox tf = (TextBox)valueWidget;
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
		if(!author.getText().isEmpty()&&!year.getText().isEmpty()&&!title.getText().isEmpty()) {
			if(k==2 && strainNumber.getText().isEmpty())
				Alerter.inputError("Please input Strain Number if you choose to input Strain Information!");
			else 
				presenter.onCreate();
		}	
		else 
			Alerter.inputError("Please input Source Document Information!");
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
		return strainNumber.getText().trim();
	}
	
	public String getEqStrainNumbers() {
		return equivalStrain.getText().trim();
	}

	public String getStrainAccession() {
		return strainAccession.getText().trim();
	}
	
	public String getStrainGenomeAccession() {
		return strainGenomeAccession.getText().trim();
	}
	
	public String getAlternativeTaxonomy() {
		return alternativeTaxonomy.getText().trim();
	}
	
	public List<TaxonIdentificationEntry> getTaxonIdentificationEntries() {
		List<TaxonIdentificationEntry> result = new LinkedList<TaxonIdentificationEntry>();
		if(ranksGrid.getRowCount() <= 2) 
			return result;
		System.out.println(ranksGrid.getRowCount());
		for(int i = 1; i <= ranksGrid.getRowCount() - 2; i++){ //row 0 is the header row, also there is a button at the end of table
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
			if(valueWidget instanceof TextBox) {
				TextBox valueBox = (TextBox)valueWidget;
				String value = valueBox.getText().trim();
				if(rank != null)
					result.add(new TaxonIdentificationEntry(rank, value));
			}
		}
		return result;
	}
	
	public List<DescriptionEntry> getDescriptionsList(){
		List<DescriptionEntry> entries = new LinkedList<DescriptionEntry>();
		System.out.println(descriptionGrid.getRowCount());
		if (descriptionGrid.getRowCount() <= 2) 
			return entries;
		for(int i = 1; i <= descriptionGrid.getRowCount() - 2; i++){
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
		ranksGrid.setWidget(1, 1,new TextBox());
		ranksGrid.setWidget(1, 2, deleteRankButton);
		ranksGrid.remove(deleteRankButton);
		authorityDateButton.setVisible(false);
	}
	
	@Override
	public void resetDescriptions() {
		while(descriptionGrid.getRowCount() > 3) {
			descriptionGrid.removeRow(descriptionGrid.getRowCount() - 2);
		}
		TextArea tArea = new TextArea();
	    tArea.setWidth(550);
	    tArea.setHeight(60);
	    tArea.setContextMenu(createMenu(tArea));
		tArea.setStyleName(descriptionArea.getStyleName());
		descriptionCombo.setValue(Description.MORPHOLOGY);
		descriptionArea.setText("");
		descriptionGrid.setWidget(1, 0, descriptionCombo);
		descriptionGrid.setWidget(1, 1, scopeCombo);
		descriptionGrid.setWidget(1, 2, tArea);
		alternativeTaxonomy.setText("");
	}
	
	@Override
	public void resetStrain() {
		createStrainRadio.setChecked(false);
		strainPanel.setVisible(false);
		strainNumber.setText("");
		equivalStrain.setText("");
		strainAccession.setText("");
		strainGenomeAccession.setText("");
		k=1;
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
	/*private boolean currentRankIsStrain() {
		Rank rank = ranksCombo.getValue();
		Set<Rank> strainRanks = new HashSet<Rank>();
		strainRanks.add(Rank.STRAIN);
		return strainRanks.contains(rank);
	}*/
	
	private boolean hasAuthorityDate(String tfValue) {
		String splits[] = tfValue.split(" ", 2);
		if(splits.length>1){
			return true;
		}
		return false;
	}

}

package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class TaxonomyComparisonInputView extends Composite implements ITaxonomyComparisonInputView {

	private static TaxonomyComparisonViewUiBinder uiBinder = GWT.create(TaxonomyComparisonViewUiBinder.class);

	interface TaxonomyComparisonViewUiBinder extends UiBinder<Widget, TaxonomyComparisonInputView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	@UiField
	VerticalPanel selectTaxonomiesPanel;
	
	@UiField
	TextBox selectExistingFolderLabel1;

	@UiField
	TextBox selectExistingFolderLabel2;
	
	@UiField
	TextBox selectExistingCleanTaxLabel;
	
	@UiField
	VerticalPanel selectCleanTaxPanel;
	
	@UiField
	Button nextButton;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	TextBox inputOntologyLabel;

	@UiField
	TextBox inputTermReviewLabel1;
	
	@UiField
	TextBox inputTermReviewLabel2;
	
	@UiField
	SubMenu subMenu;

	public TaxonomyComparisonInputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			this.glossaryListBox.addItem(taxonGroup.getDisplayName());
		}
		selectTaxonomiesPanel.setVisible(true);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	/*@UiHandler("inputButton") 
	public void onInputSelect(ClickEvent event) {
		presenter.onInputSelect();
	}*/
	
	@UiHandler("nextButton")
	public void onSearchClick(ClickEvent event) {
		presenter.onNext();
    }

	@Override
	public String getTaskName() {
		return this.taskNameTextBox.getText();
	}

	/*@Override
	public void setFilePath(String path) {
		this.inputLabel.setText(path);
	}*/

	@Override
	public void setEnabledNext(boolean value) {
		this.nextButton.setEnabled(value);
	}
	
	@Override
	public void resetFields(){
		this.taskNameTextBox.setText("");
		this.glossaryListBox.setSelectedIndex(getInitialGlossaryIndex());
	}
	
	private int getInitialGlossaryIndex() {
		for(int i=0; i<TaxonGroup.values().length; i++) {
			if(TaxonGroup.values()[i].equals(TaxonGroup.PLANT))
				return i;
		}
		return 0;
	}
	
	@UiHandler("inputOntologyButton")
	public void onOntologyButton(ClickEvent event) {
		presenter.onOntologyInput();
	}
	
	@UiHandler("inputTermReviewButton1")
	public void onTermReviewButton1(ClickEvent event) {
		presenter.onTermReviewInput1();
	}
	
	@UiHandler("inputTermReviewButton2")
	public void onTermReviewButton2(ClickEvent event) {
		presenter.onTermReviewInput2();
	}
	
	@UiHandler("selectExistingFolderButton1")
	public void onSelectExistingModel1(ClickEvent event) {
		presenter.onExistingModel1();
	}
	
	@UiHandler("selectExistingFolderButton2")
	public void onSelectExistingModel2(ClickEvent event) {
		presenter.onExistingModel2();
	}
	
	@UiHandler("selectExistingCleanTaxButton")
	public void onCleanTaxFolder(ClickEvent event) {
		presenter.onCleanTaxFolder();
	}
	
	@Override
	public void setTermReviewPath1(String text) {
		this.inputTermReviewLabel1.setText(text);
	}
	
	@Override
	public void setTermReviewPath2(String text) {
		this.inputTermReviewLabel2.setText(text);
	}
	
	@Override
	public void setOntologyPath(String text) {
		this.inputOntologyLabel.setText(text);
	}
	
	@Override
	public String getTaxonGroup() {
		return glossaryListBox.getItemText(glossaryListBox.getSelectedIndex());
	}

	@Override
	public boolean hasOntologyPath() {
		return !this.inputOntologyLabel.getText().trim().isEmpty();
	}

	/*@Override
	public boolean hasInput() {
		return !this.inputLabel.getText().trim().isEmpty();
	}*/

	@Override
	public boolean hasTermReview1() {
		return !this.inputTermReviewLabel1.getText().trim().isEmpty();
	}
	
	@Override
	public boolean hasTermReview2() {
		return !this.inputTermReviewLabel2.getText().trim().isEmpty();
	}

	@Override
	public boolean hasTaskName() {
		return !taskNameTextBox.getValue().trim().isEmpty();
	}

	@Override
	public void setSerializedModels(String serializedModel1, String serializedModel2) {
		this.selectCleanTaxPanel.setVisible(false);
		this.selectTaxonomiesPanel.setVisible(true);
		this.selectExistingFolderLabel1.setText(serializedModel1);
		this.selectExistingFolderLabel2.setText(serializedModel2);
	}

	@Override
	public void setCleanTaxPath(String shortenedPath) {
		this.selectTaxonomiesPanel.setVisible(false);
		this.selectCleanTaxPanel.setVisible(true);
		this.selectExistingCleanTaxLabel.setText(shortenedPath);
	}

	@Override
	public void setSerializedModel1(String serializedModelPath1) {
		this.selectExistingFolderLabel1.setText(serializedModelPath1);
	}
	
	@Override
	public void setSerializedModel2(String serializedModelPath2) {
		this.selectExistingFolderLabel2.setText(serializedModelPath2);
	}
}

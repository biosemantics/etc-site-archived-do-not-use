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
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class TaxonomyComparisonInputView extends Composite implements ITaxonomyComparisonInputView {

	private static TaxonomyComparisonViewUiBinder uiBinder = GWT.create(TaxonomyComparisonViewUiBinder.class);

	interface TaxonomyComparisonViewUiBinder extends UiBinder<Widget, TaxonomyComparisonInputView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	//@UiField
	//Label inputLabel;
	
	@UiField
	Button nextButton;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	Label inputOntologyLabel;

	@UiField
	Label inputTermReviewLabel1;
	
	@UiField
	Label inputTermReviewLabel2;
	
	@UiField
	SubMenu subMenu;

	public TaxonomyComparisonInputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			this.glossaryListBox.addItem(taxonGroup.getDisplayName());
		}
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
}
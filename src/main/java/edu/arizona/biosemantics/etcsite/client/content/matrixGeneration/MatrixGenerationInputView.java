package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class MatrixGenerationInputView extends Composite implements IMatrixGenerationInputView {

	private static MatrixGenerationViewUiBinder uiBinder = GWT.create(MatrixGenerationViewUiBinder.class);

	interface MatrixGenerationViewUiBinder extends UiBinder<Widget, MatrixGenerationInputView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	@UiField
	Label inputLabel;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	Button nextButton;
	
	@UiField
	Label inputOntologyLabel;

	@UiField
	Label inputTermReviewLabel;
	
	@UiField
	CheckBox inheritValuesBox;
	
	@UiField
	CheckBox generateAbsentPresentBox;
	
	@UiField
	SubMenu subMenu;
	

	public MatrixGenerationInputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			this.glossaryListBox.addItem(taxonGroup.getDisplayName());
		}
	}

	@UiHandler("inputButton") 
	public void onInputSelect(ClickEvent event) {
		presenter.onInputSelect();
	}

	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("nextButton")
	public void onSearchClick(ClickEvent event) {
		presenter.onNext();
    }
	
	@UiHandler("inputOntologyButton")
	public void onOntologyButton(ClickEvent event) {
		presenter.onOntologyInput();
	}
	
	@UiHandler("inputTermReviewButton")
	public void onTermReviewButton(ClickEvent event) {
		presenter.onTermReviewInput();
	}
	
	@Override
	public void setTermReviewPath(String text) {
		this.inputTermReviewLabel.setText(text);
	}
	
	@Override
	public void setOntologyPath(String text) {
		this.inputOntologyLabel.setText(text);
	}

	@Override
	public String getTaskName() {
		return this.taskNameTextBox.getText();
	}

	@Override
	public void setFilePath(String path) {
		this.inputLabel.setText(path);
	}

	@Override
	public void setEnabledNext(boolean value) {
		this.nextButton.setEnabled(value);
	}
	
	@Override
	public void resetFields(){
		this.taskNameTextBox.setText("");
		this.inputLabel.setText("");
		this.glossaryListBox.setSelectedIndex(getInitialGlossaryIndex());
	}

	private int getInitialGlossaryIndex() {
		for(int i=0; i<TaxonGroup.values().length; i++) {
			if(TaxonGroup.values()[i].equals(TaxonGroup.PLANT))
				return i;
		}
		return 0;
	}

	@Override
	public boolean isInheritValues() {
		return inheritValuesBox.getValue();
	}

	@Override
	public boolean isGenerateAbsentPresent() {
		return generateAbsentPresentBox.getValue();
	}
	
	@Override
	public String getTaxonGroup() {
		return glossaryListBox.getItemText(glossaryListBox.getSelectedIndex());
	}

	@Override
	public boolean hasOntologyPath() {
		return !this.inputOntologyLabel.getText().trim().isEmpty();
	}

	@Override
	public boolean hasInput() {
		return !this.inputLabel.getText().trim().isEmpty();
	}

	@Override
	public boolean hasTermReview() {
		return !this.inputTermReviewLabel.getText().trim().isEmpty();
	}

	@Override
	public boolean hasTaskName() {
		return !taskNameTextBox.getValue().trim().isEmpty();
	}
}

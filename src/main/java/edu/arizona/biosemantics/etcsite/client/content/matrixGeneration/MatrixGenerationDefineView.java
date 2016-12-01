package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

public class MatrixGenerationDefineView extends Composite implements IMatrixGenerationDefineView {

	private static MatrixGenerationViewUiBinder uiBinder = GWT.create(MatrixGenerationViewUiBinder.class);

	interface MatrixGenerationViewUiBinder extends UiBinder<Widget, MatrixGenerationDefineView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	@UiField
	TextBox inputLabel;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	Button nextButton;
	
	@UiField
	TextBox inputOntologyLabel;

	@UiField
	TextBox inputTermReviewLabel;
	
	@UiField
	CheckBox inheritValuesBox;
	
	@UiField
	CheckBox generateAbsentPresentBox;
	
	@UiField
	SubMenu subMenu;
	
	public MatrixGenerationDefineView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		
		List<TaxonGroup> groups = new ArrayList<TaxonGroup>(Arrays.asList(TaxonGroup.values()));
		Collections.sort(groups, new Comparator<TaxonGroup>() {
			@Override
			public int compare(TaxonGroup o1, TaxonGroup o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		for(TaxonGroup taxonGroup : groups) {
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
		this.taskNameTextBox.setText(null);
		//this.inputLabel.setText("");
		this.glossaryListBox.setSelectedIndex(getInitialGlossaryIndex());
		this.generateAbsentPresentBox.setValue(null);
		this.inheritValuesBox.setValue(null);
		this.inputOntologyLabel.setValue(null);
		this.inputOntologyLabel.setText(null);
		this.inputTermReviewLabel.setValue(null);
		this.inputTermReviewLabel.setText(null);
		
	}

	private int getInitialGlossaryIndex() {
		List<TaxonGroup> groups = new ArrayList<TaxonGroup>(Arrays.asList(TaxonGroup.values()));
		Collections.sort(groups, new Comparator<TaxonGroup>() {
			@Override
			public int compare(TaxonGroup o1, TaxonGroup o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		for(int i=0; i<groups.size(); i++) {
			if(groups.get(i).equals(TaxonGroup.PLANT))
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

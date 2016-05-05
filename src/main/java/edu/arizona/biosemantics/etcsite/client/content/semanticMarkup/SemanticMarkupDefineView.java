package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

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

public class SemanticMarkupDefineView extends Composite implements ISemanticMarkupDefineView {

	private static SemanticmarkupDefineViewUiBinder uiBinder = GWT
			.create(SemanticmarkupDefineViewUiBinder.class);

	interface SemanticmarkupDefineViewUiBinder extends
			UiBinder<Widget, SemanticMarkupDefineView> {
	}

	private Presenter presenter;
	
	@UiField
	CheckBox emptyGlossaryCheckbox;
	
	@UiField
	Button nextButton;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	TextBox inputLabel;
	
	@UiField 
	Button inputButton;
	
	@UiField
	TextBox taskNameTextBox;
	
	public SemanticMarkupDefineView() {
		initWidget(uiBinder.createAndBindUi(this));
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			this.glossaryListBox.addItem(taxonGroup.getDisplayName());
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	@Override
	public void setInput(String input) {
		if(input != null){
			this.inputLabel.setText(input);
		}
	}
	
	@Override
	public void setEnabledNext(boolean value) {
		this.nextButton.setEnabled(value);
	}

	@Override
	public String getTaskName() {
		return taskNameTextBox.getText();
	}

	@Override
	public String getGlossaryName() {
		return glossaryListBox.getItemText(glossaryListBox.getSelectedIndex());
	}
	
	@UiHandler("inputButton")
	public void onInput(ClickEvent event) {
		presenter.onInput();
	}
	
	@Override
	public void resetFields(){
		this.taskNameTextBox.setText("");
		//this.inputLabel.setText("");
		this.emptyGlossaryCheckbox.setValue(null);
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
	public boolean isEmptyGlossarySelected() {
		return emptyGlossaryCheckbox.getValue();
	}
}

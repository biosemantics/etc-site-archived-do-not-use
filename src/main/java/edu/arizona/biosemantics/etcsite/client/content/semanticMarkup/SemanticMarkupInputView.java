package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.semanticmarkup.know.Glossary;

public class SemanticMarkupInputView extends Composite implements ISemanticMarkupInputView {

	private enum Glossary {
		Algae, Cnidaria, Fossil, Gastropods, Hymenoptera, Plant, Porifera
	}
	
	private static SemanticmarkupInputViewUiBinder uiBinder = GWT
			.create(SemanticmarkupInputViewUiBinder.class);

	interface SemanticmarkupInputViewUiBinder extends
			UiBinder<Widget, SemanticMarkupInputView> {
	}

	private Presenter presenter;
	
	@UiField
	Button nextButton;
	
	@UiField
	ListBox glossaryListBox;
	
	@UiField
	Label inputLabel;
	
	@UiField
	Button inputButton;
	
	@UiField
	TextBox taskNameTextBox;
	
	public SemanticMarkupInputView() {
		initWidget(uiBinder.createAndBindUi(this));
		for(Glossary glossary : Glossary.values()) {
			this.glossaryListBox.addItem(glossary.toString());
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
	
	@UiHandler("inputButton")
	public void onInput(ClickEvent event) {
		presenter.onInput();
	}
	
	@UiHandler("fileManagerAnchor")
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
	}
	
	@UiHandler("fileManagerAnchor2")
	public void onFileManager2(ClickEvent event) {
		presenter.onFileManager();
	}
	
	@Override
	public void setInput(String input) {
		this.inputLabel.setText(input);
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
	
	@Override
	public void resetFields(){
		this.taskNameTextBox.setText("");
		this.inputLabel.setText("");
		this.glossaryListBox.setSelectedIndex(getInitialGlossaryIndex());
	}

	private int getInitialGlossaryIndex() {
		for(int i=0; i<Glossary.values().length; i++) {
			if(Glossary.values()[i].equals(Glossary.Plant))
				return i;
		}
		return 0;
	}
}

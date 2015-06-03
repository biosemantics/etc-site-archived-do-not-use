package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TaxonomyComparisonInputView extends Composite implements ITaxonomyComparisonInputView {

	private static TaxonomyComparisonViewUiBinder uiBinder = GWT.create(TaxonomyComparisonViewUiBinder.class);

	interface TaxonomyComparisonViewUiBinder extends UiBinder<Widget, TaxonomyComparisonInputView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	@UiField
	Label inputLabel;
	
	@UiField
	Button nextButton;
	
	@UiField
	SubMenu subMenu;

	public TaxonomyComparisonInputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("inputButton") 
	public void onInputSelect(ClickEvent event) {
		presenter.onInputSelect();
	}
	
	@UiHandler("nextButton")
	public void onSearchClick(ClickEvent event) {
		presenter.onNext();
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
	}
}
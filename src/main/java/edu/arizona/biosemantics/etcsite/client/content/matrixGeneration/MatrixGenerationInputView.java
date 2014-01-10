package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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
	Button nextButton;
	
	@UiField
	SubMenu subMenu;

	public MatrixGenerationInputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("fileManagerAnchor") 
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
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
}

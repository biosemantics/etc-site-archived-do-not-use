package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public class InputMatrixGenerationViewImpl extends MatrixGenerationViewImpl implements InputMatrixGenerationView {

	private static MatrixGenerationViewUiBinder uiBinder = GWT.create(MatrixGenerationViewUiBinder.class);

	@UiTemplate("InputMatrixGenerationView.ui.xml")
	interface MatrixGenerationViewUiBinder extends UiBinder<Widget, InputMatrixGenerationViewImpl> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox taskNameTextBox;
	
	@UiField
	Label inputLabel;
	
	@UiField
	Button nextButton;

	public InputMatrixGenerationViewImpl() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.INPUT;
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
		presenter.onNext(taskNameTextBox.getText(), inputLabel.getText());
    }

	@Override
	public HasEnabled getNextButton() {
		return nextButton;
	}

	@Override
	public HasText getInputLabel() {
		return inputLabel;
	}
}

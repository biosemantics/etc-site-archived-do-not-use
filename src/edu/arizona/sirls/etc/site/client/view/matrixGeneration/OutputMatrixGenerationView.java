package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.OutputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public class OutputMatrixGenerationView extends MatrixGenerationView implements OutputMatrixGenerationPresenter.Display {

	private Button selectButton;
	private Label fileLabel;
	private Button completeButton;

	@Override
	protected Step getStep() {
		return Step.OUTPUT;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		panel.add(new Label("Output"));
		HorizontalPanel outputPanel = new HorizontalPanel();
		this.selectButton = new Button("Select Output File");
		outputPanel.add(selectButton);
		this.fileLabel = new Label("filename");
		outputPanel.add(fileLabel);
		panel.add(outputPanel);
		this.completeButton = new Button("Complete");
		panel.add(completeButton);
		return panel;
	}

	@Override
	public Button getSelectButton() {
		return selectButton;
	}

	@Override
	public Button getCompleteButton() {
		return completeButton;
	}

	@Override
	public Label getFileLabel() {
		return fileLabel;
	}

}

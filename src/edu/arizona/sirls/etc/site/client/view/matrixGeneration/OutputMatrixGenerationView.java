package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.OutputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public class OutputMatrixGenerationView extends MatrixGenerationView implements OutputMatrixGenerationPresenter.Display {

	//private Button selectButton;
	private Label outputLabel;
	private Anchor fileManager;
	//private Button completeButton;

	@Override
	protected Step getStep() {
		return Step.OUTPUT;
	}

	@Override
	protected Widget getStepWidget() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName("contentPanel");
		
		outputLabel = new InlineLabel();
		panel.add(new InlineLabel("Matrix Generation has completed. You can find the output files in "));
		panel.add(outputLabel);
		panel.add(new InlineLabel(" in the "));
		fileManager = new Anchor(" File Manager");
		panel.add(fileManager);
		panel.add(new InlineLabel("."));
		return panel;
	}


	@Override
	public Label getOutputLabel() {
		return outputLabel;
	}

	@Override
	public Anchor getFileManager() {
		return fileManager;
	}

}

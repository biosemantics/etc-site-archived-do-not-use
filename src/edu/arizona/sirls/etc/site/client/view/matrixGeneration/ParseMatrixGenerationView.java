package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ParseMatrixGenerationPresenter;

public class ParseMatrixGenerationView extends MatrixGenerationView implements ParseMatrixGenerationPresenter.Display {

	private Button nextButton;

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.PARSE_TEXT;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Image infoImage = new Image("images/Info.gif");
		infoImage.addStyleName("infoImage");
		horizontalPanel.add(infoImage);
		horizontalPanel.add(new Label("We are now parsing the descriptions. " +
				"You will receive an email when processing has completed. You can come back to this task using the Task Manager."));
		panel.add(horizontalPanel);
		
		//only while we don't use real tasks/face process
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}
}

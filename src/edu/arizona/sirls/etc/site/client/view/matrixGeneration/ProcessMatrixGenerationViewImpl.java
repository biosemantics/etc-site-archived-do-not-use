package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;

public class ProcessMatrixGenerationViewImpl extends MatrixGenerationViewImpl implements ProcessMatrixGenerationView {

	private static ProcessingMatrixGenerationViewUiBinder uiBinder = GWT.create(ProcessingMatrixGenerationViewUiBinder.class);

	@UiTemplate("ProcessMatrixGenerationView.ui.xml")
	interface ProcessingMatrixGenerationViewUiBinder extends UiBinder<Widget, ProcessMatrixGenerationViewImpl> {
	}

	private Presenter presenter;
		
	@UiField
	Button nextButton;
	
	@UiField
	HorizontalPanel statusPanel;

	public ProcessMatrixGenerationViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.PROCESS;
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	public void onTaskManager(ClickEvent event) {
		presenter.onTaskManager();
	}

	@Override
	public void setNonResumable() {
		this.nextButton.setVisible(false);
		statusPanel.clear();
		Image statusImage = new Image("images/loader3.gif");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		FlowPanel taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are now generating a matrix. You will receive an email when processing has completed. " +
				"You can come back to this task using the  "));
		Anchor taskManagerAnchor = new Anchor("Task Manager");
		taskManagerAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onTaskManager(event);
			}
		});
		taskManagerFlowPanel.add(taskManagerAnchor);
		taskManagerFlowPanel.add(new InlineLabel("."));
	}
	
	@Override
	public void setResumable() {
		this.nextButton.setVisible(true);
		statusPanel.clear();
		Image statusImage = new Image("images/play.png");
		statusImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNext(event);
			}
		});
		statusImage.addStyleName("infoImage");
		statusImage.addStyleName("clickable");
		statusPanel.add(statusImage);
		FlowPanel taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are done generating a matrix."));
	}

}

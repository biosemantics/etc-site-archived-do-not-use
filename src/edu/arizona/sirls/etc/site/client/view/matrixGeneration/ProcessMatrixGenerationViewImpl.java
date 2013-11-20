package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;

public class ProcessMatrixGenerationViewImpl extends MatrixGenerationViewImpl implements ProcessMatrixGenerationView {

	private static ProcessingMatrixGenerationViewUiBinder uiBinder = GWT.create(ProcessingMatrixGenerationViewUiBinder.class);

	@UiTemplate("ProcessMatrixGenerationView.ui.xml")
	interface ProcessingMatrixGenerationViewUiBinder extends UiBinder<Widget, ProcessMatrixGenerationViewImpl> {
	}

	private Presenter presenter;
	
	@UiField
	Image statusImage;
	
	@UiField
	Anchor taskManagerAnchor;
	
	@UiField
	Button nextButton;

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
}

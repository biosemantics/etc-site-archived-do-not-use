package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class OutputMatrixGenerationViewImpl extends MatrixGenerationViewImpl implements OutputMatrixGenerationView {

	private static OutputMatrixGenerationViewUiBinder uiBinder = GWT.create(OutputMatrixGenerationViewUiBinder.class);

	@UiTemplate("OutputMatrixGenerationView.ui.xml")
	interface OutputMatrixGenerationViewUiBinder extends UiBinder<Widget, OutputMatrixGenerationViewImpl> {
	}

	private Presenter presenter;

	@UiField
	Anchor fileManagerAnchor;
	
	public OutputMatrixGenerationViewImpl() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.OUTPUT;
	}
	
	@UiHandler("fileManagerAnchor") 
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
	}

}

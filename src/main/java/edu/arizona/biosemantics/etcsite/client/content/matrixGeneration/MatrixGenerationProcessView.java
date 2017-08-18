package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MatrixGenerationProcessView extends Composite implements IMatrixGenerationProcessView {

	private static MatrixGenerationProcessViewUiBinder uiBinder = GWT.create(MatrixGenerationProcessViewUiBinder.class);

	interface MatrixGenerationProcessViewUiBinder extends UiBinder<Widget, MatrixGenerationProcessView> {
	}

	private Presenter presenter;
		
	@UiField
	Button reviewButton;
	
	@UiField
	Label outputLabel;
	
	@UiField
	Button outputButton;
	
	@UiField
	Button outputMCButton;
	
	@UiField
	HorizontalPanel resumablePanel;

	@UiField
	VerticalPanel nonResumablePanel;
	
	@UiField
	Anchor taskManagerAnchor;
	
	public MatrixGenerationProcessView() {
		initWidget(uiBinder.createAndBindUi(this));
		taskManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("taskManagerAnchor")
	public void onTaskManager(ClickEvent event) {
		presenter.onTaskManager();
	}

	@Override
	public void setNonResumable() {
		this.reviewButton.setVisible(false);
		this.outputButton.setVisible(false);
		this.outputMCButton.setVisible(false);
		this.nonResumablePanel.setVisible(true);
		this.resumablePanel.setVisible(false);
	}
	
	@Override
	public void setResumable() {
		this.reviewButton.setVisible(true);
		this.outputButton.setVisible(true);
		this.outputMCButton.setVisible(true);
		this.nonResumablePanel.setVisible(false);
		this.resumablePanel.setVisible(true);
	}
	
	@UiHandler("reviewButton")
	public void onReview(ClickEvent event) {
		presenter.onReview();
	}
	
	@UiHandler("outputButton")
	public void onOutput(ClickEvent event) {
		presenter.onOutput();
	}
	
	@UiHandler("outputMCButton")
	public void onOutputMC(ClickEvent event) {
		presenter.onOutputMC();
	}
	
	@Override
	public void setOutput(String value) {
		this.outputLabel.setText(value);
	}
}

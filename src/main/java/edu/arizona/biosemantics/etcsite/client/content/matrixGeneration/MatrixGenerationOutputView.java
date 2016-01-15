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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class MatrixGenerationOutputView extends Composite implements IMatrixGenerationOutputView {

	private static OutputMatrixGenerationViewUiBinder uiBinder = GWT.create(OutputMatrixGenerationViewUiBinder.class);

	interface OutputMatrixGenerationViewUiBinder extends UiBinder<Widget, MatrixGenerationOutputView> {
	}

	private Presenter presenter;

	@UiField
	Anchor fileManagerAnchor;
	
	@UiField
	InlineLabel outputLabel;
	
	@UiField
	Button publishButton;
	
	public MatrixGenerationOutputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("fileManagerAnchor") 
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
	}
	
	@UiHandler("publishButton")
	public void onPublish(ClickEvent event) {
		presenter.onPublish();
	}

	@Override
	public void setOutput(String output) {
		this.outputLabel.setText(output);
	}

}
